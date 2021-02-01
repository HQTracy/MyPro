package com.eryanet.mkotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.eryanet.common.utils.Logger

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val saab = sum(3, 4);
        Logger.debug("3+4 = $saab")
        printSum(5, 6)

        vars(1, 2, 3, 4, 5, 6)

        var a: Int = 2
        Logger.debug("a: $a")

        val b = "a is $a"
        val vl = "${b.replace("is", "was")},but now is $a"
        Logger.debug(vl)

        //类型后面加?表示可为空
        var age: String? = "23"
        //抛出空指针异常
        val ages = age!!.toInt()
        //不做处理返回 null
        val ages1 = age?.toInt()
        //age为空返回-1
        val ages2 = age?.toInt() ?: -1

        Logger.debug("ages2 is $ages2")

        for (i in 1..4){
            System.out.println(i)
        }

    }

    //    函数定义使用关键字 fun，参数格式为：参数 : 类型
//    fun sum(a: Int, b: Int): Int {
//        return a + b;
//    }
//    fun sum(a : Int, b : Int) = a + b
    public fun sum(a: Int, b: Int): Int = a + b

    //    无返回值的函数(类似Java中的void)：
//    fun printSum(a: Int, b: Int): Unit {
//        System.out.println(a + b)
//        print(a + b)
//    }
    fun printSum(a: Int, b: Int) {
        print(a + b)
    }

    fun vars(vararg v: Int) {
        for (vr in v) {
            System.out.println(vr)
        }
    }

}
