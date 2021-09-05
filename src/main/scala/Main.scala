import java.io.File
import scala.io.Source
import io.circe.generic.auto._
import io.circe.parser.decode

class RomData(val id: Int, val fc1: Seq[Int], val fc2: Seq[Int], val fc3: Seq[Int], val input: Seq[Int], val result: Seq[Double],
                val bn1_weight: Seq[Int], val bn1_bias: Seq[Int], val bn1_mean: Seq[Int], val bn1_norm: Seq[Int],
                val bn2_weight: Seq[Int], val bn2_bias: Seq[Int], val bn2_mean: Seq[Int], val bn2_norm: Seq[Int],
                val bn3_weight: Seq[Int], val bn3_bias: Seq[Int], val bn3_mean: Seq[Int], val bn3_norm: Seq[Int])

object Main extends App{
    val romDir = new File("src/test/data")
    romDir.listFiles().filter(f => f.getName().contains("params.json")).foreach { f => 
        val json = Source.fromFile(f.getAbsolutePath()).mkString
        val romData = decode[RomData](json) match {
            case Right(data) => data
            case Left(error) => throw new Exception(error)
        }
        chisel3.Driver.execute(args, () => new Linear(784, 16, romData.fc1))
    }
}
