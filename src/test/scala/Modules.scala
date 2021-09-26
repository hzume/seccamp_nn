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

class ConvData(val input: Seq[Seq[Seq[Int]]], val param: Seq[Seq[Seq[Seq[Int]]]])

class MPData(val input: Seq[Seq[Seq[Int]]])

class SBN2dData(val input:Seq[Seq[Seq[Int]]], val weight:Seq[Int], val bias:Seq[Int], val mean:Seq[Int], val norm:Seq[Int])

// class TopMLP(val in_w:Int, val out_w:Int , val input:Seq[Int], val fc1: Seq[Int], val fc2: Seq[Int], val fc3: Seq[Int],
//              val bn1_weight: Seq[Int], val bn1_bias: Seq[Int], val bn1_mean: Seq[Int], val bn1_norm: Seq[Int],
//              val bn2_weight: Seq[Int], val bn2_bias: Seq[Int], val bn2_mean: Seq[Int], val bn2_norm: Seq[Int],
//              val bn3_weight: Seq[Int], val bn3_bias: Seq[Int], val bn3_mean: Seq[Int], val bn3_norm: Seq[Int]) extends Module{
//     val io = IO(new Bundle{
//         val out = Output(Vec(10, SInt(out_w.W)))
//     })

//     val in = VecInit(input map(x => x.S(in_w.W)))
//     val mlp = Module(new MLP_p(
//             in_w, out_w, fc1, fc2, fc3, 
//             bn1_weight, bn1_bias, bn1_mean, bn1_norm, 
//             bn2_weight, bn2_bias, bn2_mean, bn2_norm, 
//             bn3_weight, bn3_bias, bn3_mean, bn3_norm
//         ))
//     mlp.io.in := in
//     io.out := mlp.io.out
// }

// class MLPTester(c: TopMLP) extends PeekPokeTester(c){
//     step(1)
//     for (i <- 0 until 10) {
//         println(s"out[$i] = ${peek(c.io.out(i))}")
//     }
// }

// class MLPSpec extends AnyFreeSpec with Matchers{
//     val romDir = new File("src/test/data")
//     romDir.listFiles().filter(f => f.getName().contains("tnn_param.json")).foreach { f => 
//         val json = Source.fromFile(f.getAbsolutePath()).mkString
//         val romData = decode[RomData](json) match {
//             case Right(data) => data
//             case Left(error) => throw new Exception(error)
//         }
//         Driver.execute(Array("--backend-name", "firrtl"), () => new TopMLP(
//             5, 10, romData.input, romData.fc1, romData.fc2, romData.fc3, 
//             romData.bn1_weight, romData.bn1_bias, romData.bn1_mean, romData.bn1_norm,
//             romData.bn2_weight, romData.bn2_bias, romData.bn2_mean, romData.bn2_norm,
//             romData.bn3_weight, romData.bn3_bias, romData.bn3_mean, romData.bn3_norm)) {
//             c => new MLPTester(c)
//         }
//     }
// }

class TopConv(val in_w:Int, val out_w:Int , val input:Seq[Seq[Seq[Int]]], val param:Seq[Seq[Seq[Seq[Int]]]]) extends Module{
    val io = IO(new Bundle{
        val out = Output(Vec(2, Vec(2, Vec(2, SInt(out_w.W)))))
    })

    val in = Wire(Vec(3, Vec(4, Vec(4, SInt(in_w.W)))))
    for (i <- 0 until 3; j <- 0 until 4; k <- 0 until 4) {
        in(i)(j)(k) := input(i)(j)(k).S(in_w.W)
    }
    val conv = Module(new Conv(4, 3, 2, 3, 1, param, in_w, out_w))
    conv.io.in := in
    io.out := conv.io.out
}

class ConvTester(c: TopConv) extends PeekPokeTester(c){
    step(1)
    for (i <- 0 until 2; j <- 0 until 2; k <- 0 until 2) {
        println(s"out[$i:$j:$k] = ${peek(c.io.out(i)(j)(k))}")
    }
}

class ConvSpec extends AnyFreeSpec with Matchers{
    val romDir = new File("src/test/data")
    romDir.listFiles().filter(f => f.getName().contains("conv.json")).foreach { f => 
        val json = Source.fromFile(f.getAbsolutePath()).mkString
        val romData = decode[ConvData](json) match {
            case Right(data) => data
            case Left(error) => throw new Exception(error)
        }
        Driver.execute(Array("--backend-name", "firrtl"), () => new TopConv(4, 8, romData.input, romData.param)) {
            c => new ConvTester(c)
        }
    }
}

class TopMP(val in_w:Int , val input:Seq[Seq[Seq[Int]]]) extends Module{
    val io = IO(new Bundle{
        val out = Output(Vec(1, Vec(2, Vec(2, SInt(in_w.W)))))
    })

    val in = Wire(Vec(1, Vec(4, Vec(4, SInt(in_w.W)))))
    for (i <- 0 until 1; j <- 0 until 4; k <- 0 until 4) {
        in(i)(j)(k) := input(i)(j)(k).S(in_w.W)
    }
    val mp = Module(new MaxPool(4, 1, 2, 2, 4))
    mp.io.in := in
    io.out := mp.io.out
}

class MPTester(c: TopMP) extends PeekPokeTester(c){
    step(1)
    for (i <- 0 until 1; j <- 0 until 2; k <- 0 until 2) {
        println(s"out[$i:$j:$k] = ${peek(c.io.out(i)(j)(k))}")
    }
}

class MPSpec extends AnyFreeSpec with Matchers{
    val romDir = new File("src/test/data")
    romDir.listFiles().filter(f => f.getName().contains("maxpool.json")).foreach { f => 
        val json = Source.fromFile(f.getAbsolutePath()).mkString
        val romData = decode[MPData](json) match {
            case Right(data) => data
            case Left(error) => throw new Exception(error)
        }
        Driver.execute(Array("--backend-name", "firrtl"), () => new TopMP(4, romData.input)) {
            c => new MPTester(c)
        }
    }
}

class TopSBN2d(val in_w:Int , val input:Seq[Seq[Seq[Int]]], val weight:Seq[Int], val bias:Seq[Int], val mean:Seq[Int], val norm:Seq[Int]) extends Module{
    val io = IO(new Bundle{
        val out = Output(Vec(1, Vec(4, Vec(4, SInt(in_w.W)))))
    })

    val in = Wire(Vec(1, Vec(4, Vec(4, SInt(in_w.W)))))
    for (i <- 0 until 1; j <- 0 until 4; k <- 0 until 4) {
        in(i)(j)(k) := input(i)(j)(k).S(in_w.W)
    }
    val sbn2d = Module(new ShiftBatchNorm2d(4, 1, weight, bias, mean, norm, 16))
    sbn2d.io.in := in
    io.out := sbn2d.io.out
}

class SBN2dTester(c: TopSBN2d) extends PeekPokeTester(c){
    step(1)
    for (i <- 0 until 1; j <- 0 until 4; k <- 0 until 4) {
        println(s"out[$i:$j:$k] = ${peek(c.io.out(i)(j)(k))}")
    }
}

class SBN2dSpec extends AnyFreeSpec with Matchers{
    val romDir = new File("src/test/data")
    romDir.listFiles().filter(f => f.getName().contains("sbn2d.json")).foreach { f => 
        val json = Source.fromFile(f.getAbsolutePath()).mkString
        val romData = decode[SBN2dData](json) match {
            case Right(data) => data
            case Left(error) => throw new Exception(error)
        }
        Driver.execute(Array("--backend-name", "firrtl"), () => new TopSBN2d(4, romData.input, romData.weight, romData.bias, romData.mean, romData.norm)) {
            c => new SBN2dTester(c)
        }
    }
}
