import chisel3._
import chisel3.iotesters.{ChiselFlatSpec, Driver, PeekPokeTester}
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.should.Matchers
import java.io.File
import scala.io.Source
import io.circe.generic.auto._
import io.circe.parser.decode
import romdatatype._
import modules._

class TopModules(val rom:MLPData) extends Module{
    val io = IO(new Bundle{
        val fc_out = Output(Vec(16, SInt(14.W)))
        val bn_bi_out = Output(Vec(16, SInt(14.W)))
        val bn_out = Output(Vec(10, SInt(14.W)))
        val fc_out0 = Output(Vec(16, SInt(14.W)))
    })

    val fc_in = VecInit(rom.test.fc_in map(x => x.S(14.W)))
    val bn_bi_in = VecInit(rom.test.bn_bi_in map(x => x.S(14.W)))
    val bn_in = VecInit(rom.test.bn_in map(x => x.S(14.W)))

    val fc0 = Module(new Linear_p0(784, 16, rom.fc(0), 4, 14))
    fc0.io.in := fc_in
    io.fc_out0 := fc0.io.out

    val fc = Module(new Linear_p(784, 16, rom.fc(0), 4))
    fc.io.in := fc_in  
    io.fc_out := fc.io.out

    val bn_bi = Module(new BN_BI(16, rom.bn(0), 14))
    bn_bi.io.in := bn_bi_in
    io.bn_bi_out := bn_bi.io.out
    
    val bn = Module(new SBN(10, rom.bn(2), 14))
    bn.io.in := bn_in
    io.bn_out := bn.io.out
}

class ModulesTester(c: TopModules) extends PeekPokeTester(c){
    step(1)
    for (i <- 0 until 16) {
        expect(c.io.fc_out(i), c.rom.test.fc_out(i))
        expect(c.io.fc_out0(i), c.rom.test.fc_out(i))
    }
    for (i <- 0 until 16) {
        expect(c.io.bn_bi_out(i), c.rom.test.bn_bi_out(i))
    }
    for (i <- 0 until 10) {
        expect(c.io.bn_out(i), c.rom.test.bn_out(i))
    }

}

class ModulesSpec extends AnyFreeSpec with Matchers{
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
    Driver.execute(Array("--backend-name", "firrtl"), () => new TopModules(rom)) {
        c => new ModulesTester(c)
    }
}