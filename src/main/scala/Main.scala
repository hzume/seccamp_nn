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
    romDir.listFiles().filter(f => f.getName().contains("params_4.json")).foreach { f => 
        val json = Source.fromFile(f.getAbsolutePath()).mkString
        val romData = decode[RomData](json) match {
            case Right(data) => data
            case Left(error) => throw new Exception(error)
        }
        chisel3.Driver.execute(args, () => new MLP(
            4, 8, romData.fc1, romData.fc2, romData.fc3, 
            romData.bn1_weight, romData.bn1_bias, romData.bn1_mean, romData.bn1_norm,
            romData.bn2_weight, romData.bn2_bias, romData.bn2_mean, romData.bn2_norm,
            romData.bn3_weight, romData.bn3_bias, romData.bn3_mean, romData.bn3_norm))
        //chisel3.Driver.execute(args, () => new Linear_p(784, 16, romData.fc1, 4, 12))
    }
    
    
}
