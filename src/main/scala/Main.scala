import java.io.File
import scala.io.Source
import io.circe.generic.auto._
import io.circe.parser.decode

class RomData(val id: Int, val fc1: Seq[Int], val fc2: Seq[Int], val fc3: Seq[Int], val input: Seq[Int], val result: Seq[Double],
                val bn1_weight: Seq[Int], val bn1_bias: Seq[Int], val bn1_mean: Seq[Int], val bn1_norm: Seq[Int],
                val bn2_weight: Seq[Int], val bn2_bias: Seq[Int], val bn2_mean: Seq[Int], val bn2_norm: Seq[Int],
                val bn3_weight: Seq[Int], val bn3_bias: Seq[Int], val bn3_mean: Seq[Int], val bn3_norm: Seq[Int])

class ConvData(val input: Seq[Int], val param: Seq[Seq[Seq[Seq[Int]]]])

class CnnData(val input: Seq[Seq[Seq[Int]]], val label:Int, val cnn_param:Seq[Seq[Seq[Seq[Seq[Int]]]]], val cnn_bn:Seq[Seq[Seq[Int]]], val fc_param:Seq[Seq[Int]], val fc_bn:Seq[Seq[Seq[Int]]])

object Main extends App{
    val romDir = new File("src/test/data")
    romDir.listFiles().filter(f => f.getName().contains("cnn_data.json")).foreach { f => 
        val json = Source.fromFile(f.getAbsolutePath()).mkString
        val romData = decode[CnnData](json) match {
            case Right(data) => data
            case Left(error) => throw new Exception(error)
        }
        chisel3.Driver.execute(args, () => new CNN(5, 16, romData.cnn_param, romData.cnn_bn, romData.fc_param, romData.fc_bn))
    }

}
