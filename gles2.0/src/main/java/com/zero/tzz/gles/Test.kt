package com.zero.tzz.gles

import java.io.*
import java.text.SimpleDateFormat
import java.util.*


/**
 *
 * @author Zero_Tzz
 * @date 2019-12-05 13:02
 * @description Test
 */
const val INPUT_SYSTEM_LOG = "/sdcard/system-log.txt"
const val OUTPUT_SYSTEM_LOG = "/sdcard/testlog/system-log-new.txt"

const val INPUT_VIDEOSTREAM_LOG = "/sdcard/videostream-log.txt"
const val OUTPUT_VIDEOSTREAM_LOG = "/sdcard/testlog/videostream-log-new.txt"

const val FLAG_VIDEO_STREAM_DIFF = 1
const val FLAG_VIDEO_STREAM_PER_SECOND = 2

const val FLAG_SYSTEM_DIFF = 3
const val FLAG_SYSTEM_PER_SECOND = 4

object Test {

    fun output(flag: Int) {
        if (!File("/sdcard/testlog").exists()) {
            File("/sdcard/testlog").mkdirs()
        }
        var input = INPUT_VIDEOSTREAM_LOG
        var output = OUTPUT_VIDEOSTREAM_LOG
        if (flag == FLAG_VIDEO_STREAM_DIFF || flag == FLAG_VIDEO_STREAM_PER_SECOND) {
            input = INPUT_VIDEOSTREAM_LOG
            output = OUTPUT_VIDEOSTREAM_LOG
        } else if (flag == FLAG_SYSTEM_DIFF || flag == FLAG_SYSTEM_PER_SECOND) {
            input = INPUT_SYSTEM_LOG
            output = OUTPUT_SYSTEM_LOG
        }

        val sb = StringBuffer()
        try {
            val br = BufferedReader(
                InputStreamReader(
                    FileInputStream(File(input)),
                    "UTF-8"
                )
            )
            var lineTxt = br.readLine()
            var lastTime = ""
            var frameCount = 0
            while (lineTxt != null) {
                if (lineTxt.contains("[TVDBG] DequeueOutputBuffer")) {
                    // 输出 帧时间差
                    if (flag == FLAG_VIDEO_STREAM_DIFF || flag == FLAG_SYSTEM_DIFF) {
                        try {
                            val sp1 = lineTxt.split("D/")
//                            val sp2 = sp1[0].split(".")
                            if (lastTime.isNotEmpty()) {
                                val sec = dateToStamp(sp1[0]) - dateToStamp(lastTime)
                                sb.append("时间差：").append(sec)
                                    .append(" ms")
                                    .append(" ------- ")
                                    .append(sp1[0])
                                    .append(" - ")
                                    .append(lastTime)
                                    .append("   size = ${lineTxt.split("size=")[1]}")
                                    .append("\n")
                            }
                            lastTime = sp1[0]
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                    // 输出 每秒的帧数
                    if (flag == FLAG_VIDEO_STREAM_PER_SECOND || flag == FLAG_SYSTEM_PER_SECOND) {
                        if (!lineTxt.contains("size=-1")){
                            try {
                                val sp1 = lineTxt.split("D/")
    //                            val sp2 = sp1[0].split(".")
                                frameCount++
                                if (lastTime.isNotEmpty()) {
                                    val sec = dateToStamp(sp1[0]) - dateToStamp(lastTime)
                                    if (sec >= 1000) {
                                        sb.append("每秒帧数：").append(frameCount)
                                            .append(" 帧").append(" ------- ")
                                            .append(sp1[0])
                                            .append(" - ")
                                            .append(lastTime)
                                            .append("\n")
                                        lastTime = sp1[0]
                                        frameCount = 0
                                    }
                                } else {
                                    lastTime = sp1[0]
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
                lineTxt = br.readLine()
            }
            br.close()
            println("读完")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        /* 输出数据 */
        try {
            val bw = BufferedWriter(
                OutputStreamWriter(
                    FileOutputStream(File(output + flag)),
                    "UTF-8"
                )
            )
            bw.write(sb.toString())
            bw.close()
            println("写完")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    val simpleDateFormat = SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.CHINA)
    private fun dateToStamp(s: String): Long {
        return simpleDateFormat.parse(s).time
    }
}