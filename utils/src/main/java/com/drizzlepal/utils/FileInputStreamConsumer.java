package com.drizzlepal.utils;

import java.io.InputStream;

/**
 * 功能描述: 定义了一个函数式接口，用于处理文件输入流
 * 
 * 该接口的目的在于提供一个标准的方式，使得实现者可以定义如何处理一个输入流。
 * 这种设计允许在不同的上下文中重用和灵活地处理输入流数据。
 * 
 * @FunctionalInterface 表示只有一个抽象方法的接口，可以作为Lambda表达式的目标类型
 */
@FunctionalInterface
public interface FileInputStreamConsumer {

    /**
     * 方法描述: 处理一个文件输入流
     * 
     * 该方法接受一个InputStream对象作为参数，并执行相关的处理操作。实现者可以根据需要，
     * 对输入流执行任何操作，例如读取数据、解析内容或进行转换。
     * 
     * @param in 文件输入流，用于读取文件数据
     * @throws Exception 如果在处理输入流时发生错误，则抛出异常
     */
    void accept(InputStream in) throws Exception;

}