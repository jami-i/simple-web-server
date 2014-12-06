package com.github.jamii.sws

import java.io.File
import java.nio.file.Files

import org.jboss.netty.buffer.{ChannelBuffer, ChannelBuffers}

object Filer {

  val dirIndexes = List("index.html", "index.htm")

  def path2File(base: String, path:String):Option[File] = {
    val f = new File(base + path)

    f.isDirectory match{
      case false => if (f.canRead) Some(f) else None
      case true => f.listFiles().find(file => dirIndexes.contains(file.getName)).headOption
    }

  }

  def file2Bytes(file:java.io.File):Array[Byte] = Files.readAllBytes(file.toPath)
  def file2Buff(file:java.io.File):Option[ChannelBuffer] = {
    val arr = file2Bytes(file)
    if(arr.isEmpty) None
    else Some(ChannelBuffers.copiedBuffer(arr))
  }

  def open(base: String, path:String):Option[ChannelBuffer] = {
    path2File(base, path).flatMap(file2Buff)
  }

}
