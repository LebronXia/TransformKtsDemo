package com.xiamu.transform.base

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import java.io.File
import org.apache.commons.io.FileUtils
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.ForkJoinPool
import com.xiamu.transform.utils.Log
import com.xiamu.transform.utils.ClassUtils
import com.xiamu.transform.utils.DigestUtils
import org.apache.commons.io.IOUtils
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream

/**
 * Created by zxb in 2022/2/23
 */
abstract class BaseTransform : Transform(){

    //创建一个通用池
    private val executorService : ExecutorService = ForkJoinPool.commonPool()
    private val taskList = mutableListOf<Callable<Unit>>()

    //返回对应的 Task 名称。
    override fun getName(): String = javaClass.simpleName

    //确定对那些类型的结果进行转换。
    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    //指定插件的适用范围。
    override fun getScopes(): MutableSet<in QualifiedContent.Scope>{
        return mutableSetOf(
            QualifiedContent.Scope.PROJECT,
            QualifiedContent.Scope.SUB_PROJECTS,
            QualifiedContent.Scope.EXTERNAL_LIBRARIES
        )
    }

    //表示是否支持增量更新。
    override fun isIncremental(): Boolean = true

    //进行具体的转换过程。
    override fun transform(transformInvocation: TransformInvocation) {
        Log.log("transform start--------------->")
        onTransformStart()

        val outputProvider = transformInvocation.outputProvider
        val context = transformInvocation.context
        val isIncremental = transformInvocation.isIncremental
        val startTime = System.currentTimeMillis()

        //不是增量更新，删除之前输出
        if (!isIncremental){
            outputProvider.deleteAll()
        }

        transformInvocation?.inputs?.forEach{input ->
            //输入为文件夹类型   (本地 project 编译成的多个 class ⽂件存放的目录）
            input.directoryInputs.forEach{directoryInput ->
                submitTask {
                    handleDirectory(directoryInput, outputProvider, context, isIncremental)
                }
            }

            //输入为jar包类型  （各个依赖所编译成的 jar 文件）
            input.jarInputs.forEach{jarInput ->
                submitTask {
                    handleJar(jarInput, outputProvider, context, isIncremental)
                }
            }
        }

        val taskListFeature = executorService.invokeAll(taskList)
        taskListFeature.forEach{
            it.get()
        }
        onTransformEnd()
        Log.log("transform end--------------->" + "duration : " + (System.currentTimeMillis() - startTime) + " ms")
    }

    protected open fun onTransformStart() {

    }

    protected open fun onTransformEnd() {

    }

    private fun handleJar(jarInput: JarInput, outputProvider: TransformOutputProvider, context: Context, isIncremental: Boolean) {
        //得到上一个Transform输入文件
        val inputJar = jarInput.file
        //得到当前Transform输出jar文件
        val outputJar = outputProvider.getContentLocation(
            jarInput.name, jarInput.contentTypes,
            jarInput.scopes, Format.JAR
        )

        //增量处理
        if (isIncremental){
            when(jarInput.status){
                //文件没有改变
                Status.NOTCHANGED -> {

                }
                //有修改文件
                Status.ADDED,Status.CHANGED -> {

                }
                //文件被移除
                Status.REMOVED -> {
                    if (outputJar.exists()){
                        FileUtils.forceDelete(outputJar)
                    }
                    return
                }
                else -> {
                    return
                }
            }
        }

        if (outputJar.exists()){
            FileUtils.forceDelete(outputJar)
        }

        //修改后的文件
        val modifiedJar = if (ClassUtils.isLegalJar(jarInput.file)) {
            modifyJar(jarInput.file, context.temporaryDir)
        } else {
            Log.log("不处理： " + jarInput.file.absoluteFile)
            jarInput.file
        }
        FileUtils.copyFile(modifiedJar, outputJar)
    }

    private fun handleDirectory(directoryInput: DirectoryInput, outputProvider: TransformOutputProvider, context: Context, isIncremental: Boolean) {

        //得到上一个Transform输入文件目录
        val inputDir = directoryInput.file
        //得到当前Transform
        val outputDir = outputProvider.getContentLocation(
            directoryInput.name, directoryInput.contentTypes,
            directoryInput.scopes, Format.DIRECTORY
        )

        val srcDirPath = inputDir.absolutePath
        val destDirPath = outputDir.absolutePath
        //写入临时文件的目录
        val temporaryDir = context.temporaryDir

        //创建目录
        FileUtils.forceMkdir(outputDir)
        if (isIncremental){
            val changedFilesMap = directoryInput.changedFiles
            for (mutableEntry in changedFilesMap){
                val inputFile = mutableEntry.key
                //最终文件应该存放的路径
//                val destFilePath = inputFile.absolutePath.replace(srcDirPath, destDirPath)
//                val destFile = File(destFilePath)

                when(mutableEntry.value){
                    Status.ADDED, Status.CHANGED ->{
                        //处理class文件
                        modifyClassFile(inputFile, srcDirPath, destDirPath, temporaryDir)
                    }
                    Status.REMOVED -> {
                        val destFilePath = inputFile.absolutePath.replace(srcDirPath, destDirPath)
                        val destFile = File(destFilePath)
                        if (destFile.exists()){
                            destFile.delete()
                        }
                        continue
                    }
                    Status.NOTCHANGED -> {
                        continue
                    }
                    else -> {
                        continue
                    }
                }
            }

        } else {
            //过滤出是文件的，而不是目录
            directoryInput.file.walkTopDown().filter { it.isFile }
                .forEach { classFile ->
                    modifyClassFile(classFile, srcDirPath, destDirPath, temporaryDir)
                }
        }
    }

    //处理 jar
    private fun modifyJar(jarFile: File, temporaryDir: File): File {
        Log.log("处理 jar： " + jarFile.absoluteFile)
        //存放临时操作的class文件 当操作完毕，便将临时文件拷贝到dest文件
        val tempOutputJarFile = File(temporaryDir, DigestUtils.generateClassFileName(jarFile))
        //避免上次的缓存被重复插入
        if (tempOutputJarFile.exists()){
            tempOutputJarFile.delete()
        }

        //利用jarInputStream生成jar文件写入内容
        val jarOutputStream = JarOutputStream(FileOutputStream(tempOutputJarFile))
        val inputJarFile = JarFile(jarFile, false)

        try {
            //拿到所有的jar中的文件
            val enumeration = inputJarFile.entries()
            //用于保存jar文件，修改jar中的class
            while (enumeration.hasMoreElements()){
                //得到下个jar结点
                val jarEntry = enumeration.nextElement()
                val jarEntryName = jarEntry.name
                if (jarEntryName.endsWith(".DSA") || jarEntryName.endsWith(".SF")){

                } else {
                    //读取jar中的文件输入流
                    val inputStream = inputJarFile.getInputStream(jarEntry)
                    try {
                        val sourceClassBytes = IOUtils.toByteArray(inputStream)

                        //拿到修改后的临时文件
                        val modifiedClassBytes =
                            if (jarEntry.isDirectory || !ClassUtils.isLegalClass(jarEntryName)) {
                                null
                            } else {
                                modifyClass(sourceClassBytes)
                            }
                        jarOutputStream.putNextEntry(JarEntry(jarEntryName))
                        jarOutputStream.write(modifiedClassBytes ?: sourceClassBytes)
                        jarOutputStream.closeEntry()
                    } finally {
                        IOUtils.closeQuietly(inputStream)
                    }
                }
            }
        } finally {
            jarOutputStream.flush()
            IOUtils.closeQuietly(jarOutputStream)
            IOUtils.closeQuietly(inputJarFile)
        }
        return tempOutputJarFile
    }

    //处理 class
    private fun modifyClassFile(classFile: File, srcDirPath: String, destDirPath: String, temporaryDir: File) {
        Log.log("处理 class： " + classFile.absoluteFile)
        //最终的文件
        val destFilePath = classFile.absolutePath.replace(srcDirPath, destDirPath)
        val destFile = File(destFilePath)
        if (destFile.exists()){
            destFile.delete()
        }

        //拿到修改后的临时文件
        val modifyClassFile = if (ClassUtils.isLegalClass(classFile)){
            modifyClass(classFile, temporaryDir)
        } else {
            null
        }

        //将文件放倒最终地址目录
        FileUtils.copyFile(modifyClassFile ?: classFile, destFile)
        //删除临时文件
        modifyClassFile?.delete()

    }

    private fun modifyClass(classFile: File, temporaryDir: File): File {
        //对输入文件进行修改
        val byteArray = IOUtils.toByteArray(FileInputStream(classFile))
        val modifiedByteArray = modifyClass(byteArray)
        //MD5重命名输出文件,因为可能同名,会覆盖
        val modifiedFile = File(temporaryDir, DigestUtils.generateClassFileName(classFile))

        if (modifiedFile.exists()){
            modifiedFile.delete()
        }

        //临时文件写入
        modifiedFile.createNewFile()
        //修改输入文件完毕复制输出文件中
        val fos = FileOutputStream(modifiedFile)
        fos.write(modifiedByteArray)
        fos.close()
        return modifiedFile
    }

    private fun submitTask(task: () -> Unit){
        taskList.add(Callable<Unit>{
            task
        })
    }

    protected abstract fun modifyClass(byteArray: ByteArray): ByteArray

}