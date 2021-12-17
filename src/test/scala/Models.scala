import chisel3._
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import java.io.File
import scala.io.Source
import io.circe.generic.auto._
import io.circe.parser.decode
import romdatatype._

class TopMLP(val rom:MLPData, val config:Config, val out_dim:Int=10) extends Module{
    val io = IO(new Bundle{
        val out = Output(Vec(out_dim, SInt(10.W)))
    })

    val in = VecInit(rom.test.input map(x => x.S(rom.model_arch.in_w.W)))
    val mlp = Module(new MLP(rom, config))
    mlp.io.in := in
    io.out := mlp.io.out
}

class MLPTester(c: TopMLP) extends PeekPokeTester(c){
    step(1)
    for (i <- 0 until 10) {
        println(s"out[$i] = ${peek(c.io.out(i))}")
        expect(c.io.out(i), c.rom.test.result(i))
    }
}

class MLPSpec extends AnyFreeSpec with Matchers{
    val configFile = new File("src/config/config.json")
    val configJson = Source.fromFile(configFile.getAbsolutePath()).mkString
    val config = decode[Config](configJson) match {
        case Right(data) => data
        case Left(error) => throw new Exception(error)
    }

    val romFile = new File("src/parameters/" + config.test_param_id + ".json")
    val romJson = Source.fromFile(romFile.getAbsolutePath()).mkString
    val rom = decode[MLPData](romJson) match {
        case Right(data) => data
        case Left(error) => throw new Exception(error)
    }
    Driver.execute(Array("--backend-name", "firrtl"), () => new TopMLP(rom, config)) {
        c => new MLPTester(c)
    }
}

// class TopConv(val in_w:Int, val out_w:Int , val input:Seq[Seq[Seq[Int]]], val param:Seq[Seq[Seq[Seq[Int]]]]) extends Module{
//     val io = IO(new Bundle{
//         val out = Output(Vec(2, Vec(2, Vec(2, SInt(out_w.W)))))
//     })

//     val in = Wire(Vec(3, Vec(4, Vec(4, SInt(in_w.W)))))
//     for (i <- 0 until 3; j <- 0 until 4; k <- 0 until 4) {
//         in(i)(j)(k) := input(i)(j)(k).S(in_w.W)
//     }
//     val conv = Module(new Conv(4, 3, 2, 3, 1, param, in_w, out_w))
//     conv.io.in := in
//     io.out := conv.io.out
// }

// class ConvTester(c: TopConv) extends PeekPokeTester(c){
//     step(1)
//     for (i <- 0 until 2; j <- 0 until 2; k <- 0 until 2) {
//         println(s"out[$i:$j:$k] = ${peek(c.io.out(i)(j)(k))}")
//     }
// }

// class ConvSpec extends AnyFreeSpec with Matchers{
//     val romDir = new File("src/test/data")
//     romDir.listFiles().filter(f => f.getName().contains("conv.json")).foreach { f => 
//         val json = Source.fromFile(f.getAbsolutePath()).mkString
//         val romData = decode[ConvData](json) match {
//             case Right(data) => data
//             case Left(error) => throw new Exception(error)
//         }
//         Driver.execute(Array("--backend-name", "firrtl"), () => new TopConv(4, 8, romData.input, romData.param)) {
//             c => new ConvTester(c)
//         }
//     }
// }

// class TopMP(val in_w:Int , val input:Seq[Seq[Seq[Int]]]) extends Module{
//     val io = IO(new Bundle{
//         val out = Output(Vec(1, Vec(2, Vec(2, SInt(in_w.W)))))
//     })

//     val in = Wire(Vec(1, Vec(4, Vec(4, SInt(in_w.W)))))
//     for (i <- 0 until 1; j <- 0 until 4; k <- 0 until 4) {
//         in(i)(j)(k) := input(i)(j)(k).S(in_w.W)
//     }
//     val mp = Module(new MaxPool(4, 1, 2, 2, 4))
//     mp.io.in := in
//     io.out := mp.io.out
// }

// class MPTester(c: TopMP) extends PeekPokeTester(c){
//     step(1)
//     for (i <- 0 until 1; j <- 0 until 2; k <- 0 until 2) {
//         println(s"out[$i:$j:$k] = ${peek(c.io.out(i)(j)(k))}")
//     }
// }

// class MPSpec extends AnyFreeSpec with Matchers{
//     val romDir = new File("src/test/data")
//     romDir.listFiles().filter(f => f.getName().contains("maxpool.json")).foreach { f => 
//         val json = Source.fromFile(f.getAbsolutePath()).mkString
//         val romData = decode[MPData](json) match {
//             case Right(data) => data
//             case Left(error) => throw new Exception(error)
//         }
//         Driver.execute(Array("--backend-name", "firrtl"), () => new TopMP(4, romData.input)) {
//             c => new MPTester(c)
//         }
//     }
// }

// class TopSBN2d(val in_w:Int , val input:Seq[Seq[Seq[Int]]], val weight:Seq[Int], val bias:Seq[Int], val mean:Seq[Int], val norm:Seq[Int]) extends Module{
//     val io = IO(new Bundle{
//         val out = Output(Vec(1, Vec(4, Vec(4, SInt(in_w.W)))))
//     })

//     val in = Wire(Vec(1, Vec(4, Vec(4, SInt(in_w.W)))))
//     for (i <- 0 until 1; j <- 0 until 4; k <- 0 until 4) {
//         in(i)(j)(k) := input(i)(j)(k).S(in_w.W)
//     }
//     val sbn2d = Module(new ShiftBatchNorm2d(4, 1, weight, bias, mean, norm, 16))
//     sbn2d.io.in := in
//     io.out := sbn2d.io.out
// }

// class SBN2dTester(c: TopSBN2d) extends PeekPokeTester(c){
//     step(1)
//     for (i <- 0 until 1; j <- 0 until 4; k <- 0 until 4) {
//         println(s"out[$i:$j:$k] = ${peek(c.io.out(i)(j)(k))}")
//     }
// }

// class SBN2dSpec extends AnyFreeSpec with Matchers{
//     val romDir = new File("src/test/data")
//     romDir.listFiles().filter(f => f.getName().contains("sbn2d.json")).foreach { f => 
//         val json = Source.fromFile(f.getAbsolutePath()).mkString
//         val romData = decode[SBN2dData](json) match {
//             case Right(data) => data
//             case Left(error) => throw new Exception(error)
//         }
//         Driver.execute(Array("--backend-name", "firrtl"), () => new TopSBN2d(4, romData.input, romData.weight, romData.bias, romData.mean, romData.norm)) {
//             c => new SBN2dTester(c)
//         }
//     }
// }
