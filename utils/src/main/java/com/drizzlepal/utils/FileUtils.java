package com.drizzlepal.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 文件工具类，提供文件操作的辅助方法
 */
public class FileUtils {

    /**
     * 使用 FileInputStreamConsumer 处理文件输入流
     * 此方法通过SeekableByteChannel创建InputStream，然后使用提供的消费者函数进行处理
     * 确保在处理完后关闭流，避免资源泄露
     *
     * @param absolutePath  文件的绝对路径
     * @param streamHandler 消费者函数，用于处理输入流
     * @throws Exception 如果文件处理过程中发生错误，则抛出异常
     */
    public static void fileInputStreamHandler(String absolutePath, FileInputStreamConsumer streamHandler)
            throws Exception {
        // 获取文件的路径
        Path path = Paths.get(absolutePath);
        // 使用try-with-resources确保资源在使用后被正确关闭
        try (
                // 创建SeekableByteChannel以读取文件
                SeekableByteChannel sbc = Files.newByteChannel(path);
                // 使用SeekableByteChannel创建InputStream
                InputStream in = Channels.newInputStream(sbc);) {
            // 使用提供的消费者函数处理输入流
            streamHandler.accept(in);
        }
    }

    /**
     * 将输入流保存到指定的文件路径
     * 此方法用于处理文件流的保存操作，将输入流中的数据复制到指定的文件路径中
     * 主要解决了如何将流中的数据持久化到文件系统中的问题
     *
     * @param inputStream  输入流，包含要保存的数据
     * @param absolutePath 文件的绝对路径，指示数据保存的位置
     */
    public static void saveInputStreamToFile(InputStream inputStream, String absolutePath) {
        // 获取文件的路径
        Path path = Paths.get(absolutePath);
        try {
            // 将输入流中的数据复制到指定的路径中，如果文件存在，将会被覆盖
            Files.copy(inputStream, path);
        } catch (Exception e) {
            // 打印异常信息，便于调试和日志记录
            e.printStackTrace();
        }
    }

    /**
     * 获取类路径下的文件没有则创建文件
     *
     * @param fileName 文件名
     * @return 文件
     * @throws IOException
     */
    public static File getClassPathFile(String fileName) throws IOException {
        URL resource = FileUtils.class.getClassLoader().getResource(".");
        File file = new File(resource.getPath(), fileName);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    /**
     * 删除文件
     * 
     * @param file 要删除的文件
     */
    public static void deleteFile(File file) {
        if (file.exists()) {
            file.delete();
        }
    }

}