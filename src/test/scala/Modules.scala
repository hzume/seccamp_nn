import chisel3._
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import java.io.File
import scala.io.Source
import io.circe.generic.auto._
import io.circe.parser.decode

class RomData(val id: Int, val fc1: Seq[Int], val fc2: Seq[Int], val fc3: Seq[Int], val input: Seq[Int], val result: Seq[Double],
                val bn1_weight: Seq[Int], val bn1_bias: Seq[Int], val bn1_mean: Seq[Int], val bn1_norm: Seq[Int],
                val bn2_weight: Seq[Int], val bn2_bias: Seq[Int], val bn2_mean: Seq[Int], val bn2_norm: Seq[Int],
                val bn3_weight: Seq[Int], val bn3_bias: Seq[Int], val bn3_mean: Seq[Int], val bn3_norm: Seq[Int])

class TopMLP(val in_w:Int, val input:Seq[Int], val fc1: Seq[Int], val fc2: Seq[Int], val fc3: Seq[Int],
             val bn1_weight: Seq[Int], val bn1_bias: Seq[Int], val bn1_mean: Seq[Int], val bn1_norm: Seq[Int],
             val bn2_weight: Seq[Int], val bn2_bias: Seq[Int], val bn2_mean: Seq[Int], val bn2_norm: Seq[Int],
             val bn3_weight: Seq[Int], val bn3_bias: Seq[Int], val bn3_mean: Seq[Int], val bn3_norm: Seq[Int]) extends Module{
    val io = IO(new Bundle{
        val out = Output(Vec(10, SInt(16.W)))
    })

    val in = VecInit(input map(x => x.S(in_w.W)))
    val mlp = Module(new MLP(
            fc1, fc2, fc3, 
            bn1_weight, bn1_bias, bn1_mean, bn1_norm, 
            bn2_weight, bn2_bias, bn2_mean, bn2_norm, 
            bn3_weight, bn3_bias, bn3_mean, bn3_norm
        ))
    mlp.io.in := in
    io.out := mlp.io.out
}

class MLPTester(c: TopMLP) extends PeekPokeTester(c){
    step(1)
    for (i <- 0 until 10) {
        println(s"out[$i] = ${peek(c.io.out(i))}")
    }
}

class MLPSpec extends AnyFreeSpec with Matchers{
    val romDir = new File("src/test/data")
    romDir.listFiles().filter(f => f.getName().contains("params_4.json")).foreach { f => 
        val json = Source.fromFile(f.getAbsolutePath()).mkString
        val romData = decode[RomData](json) match {
            case Right(data) => data
            case Left(error) => throw new Exception(error)
        }
        Driver.execute(Array("--backend-name", "firrtl"), () => new TopMLP(
            4, 8, romData.input, romData.fc1, romData.fc2, romData.fc3, 
            romData.bn1_weight, romData.bn1_bias, romData.bn1_mean, romData.bn1_norm,
            romData.bn2_weight, romData.bn2_bias, romData.bn2_mean, romData.bn2_norm,
            romData.bn3_weight, romData.bn3_bias, romData.bn3_mean, romData.bn3_norm)) {
            c => new MLPTester(c)
        }
    }
}

/*
class TopLinear(val param: Seq[Int], val input: Seq[Int]) extends Module{
    val io = IO(new Bundle{
        val out = Output(Vec(16, SInt(16.W)))
    })

    val in = VecInit(input map(x => x.S(16.W)))
    val linear = Module(new Linear(784, 16, param))
    linear.io.in := in
    io.out := linear.io.out
}

class LinearTester(c: TopLinear) extends PeekPokeTester(c){
    step(1)
    for (i <- 0 until 16) {
        println(s"out[$i] = ${peek(c.io.out(i))}")
    }
}

class LinearSpec extends AnyFreeSpec with Matchers{
    val romDir = new File("src/test/data")
    romDir.listFiles().filter(f => f.getName().contains("params.json")).foreach { f => 
        val json = Source.fromFile(f.getAbsolutePath()).mkString
        val romData = decode[RomData](json) match {
            case Right(data) => data
            case Left(error) => throw new Exception(error)
        }
        Driver.execute(Array("--backend-name", "firrtl"), () => new TopLinear(romData.fc1, romData.input)) {
            c => new LinearTester(c)
        }
    }
}
*/
