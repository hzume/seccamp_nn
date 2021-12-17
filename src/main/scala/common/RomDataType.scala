package romdatatype

class Config(val test_param_id: String, val build_param_id: String, val parallelize: Boolean)

class RomData(val id: Int, val input: Seq[Int], val result: Seq[Int], val fc1: Seq[Int], val fc2: Seq[Int], val fc3: Seq[Int], 
                val bn1_weight: Seq[Int], val bn1_bias: Seq[Int], val bn1_mean: Seq[Int], val bn1_norm: Seq[Int],
                val bn2_weight: Seq[Int], val bn2_bias: Seq[Int], val bn2_mean: Seq[Int], val bn2_norm: Seq[Int],
                val bn3_weight: Seq[Int], val bn3_bias: Seq[Int], val bn3_mean: Seq[Int], val bn3_norm: Seq[Int])

class ConvData(val input: Seq[Int], val param: Seq[Seq[Seq[Seq[Int]]]])

class CnnData(val input: Seq[Seq[Seq[Int]]], val label:Int, val cnn_param:Seq[Seq[Seq[Seq[Seq[Int]]]]], val cnn_bn:Seq[Seq[Seq[Int]]], val fc_param:Seq[Seq[Int]], val fc_bn:Seq[Seq[Seq[Int]]])

class MLP3Data(val id: Int, val fc1: Seq[Int], val fc2: Seq[Int], val fc3: Seq[Int], val input: Seq[Int], val result: Seq[Double],
                val threshold1:Seq[Int], val threshold2:Seq[Int],
                val bn3_weight: Seq[Int], val bn3_bias: Seq[Int], val bn3_mean: Seq[Int], val bn3_norm: Seq[Int])

class ModelArch(val hidden_dim: Int, val hidden_layers: Int, val precalculate_bn: Boolean, val in_w: Int)

class BNParameter(val weight: Seq[Int], val bias: Seq[Int], val mean: Seq[Int], val norm: Seq[Int], val threshold: Seq[Int], val gamma: Seq[Int])

class TestIO(val input: Seq[Int], val result: Seq[Int],
                val fc_in: Seq[Int], val fc_out: Seq[Int],
                val bn_bi_in: Seq[Int], val bn_bi_out: Seq[Int],
                val bn_in: Seq[Int], val bn_out: Seq[Int])

class MLPData(val model_arch: ModelArch, val fc: Seq[Seq[Seq[Int]]], val bn: Seq[BNParameter], val test: TestIO)

