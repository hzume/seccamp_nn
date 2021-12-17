import java.io.File
import scala.io.Source
import io.circe.generic.auto._
import io.circe.parser.decode
import romdatatype._

object Main extends App{
    val configFile = new File("src/config/build_config.json")
    val configJson = Source.fromFile(configFile.getAbsolutePath()).mkString
    val config = decode[Config](configJson) match {
        case Right(data) => data
        case Left(error) => throw new Exception(error)
    }

    val romFile = new File("src/parameters/" + config.build_param_id + ".json")
    val romJson = Source.fromFile(romFile.getAbsolutePath()).mkString
    val rom = decode[MLPData](romJson) match {
        case Right(data) => data
        case Left(error) => throw new Exception(error)
    }
    chisel3.Driver.execute(args, () => new MLP(rom, config))
}
