## 学習
train_scripts/config/train_config.jsonで設定を変更する。

> python train_scripts/training_mnist.py -c {コメント}

を実行。

train_scripts/logにlogが保存される。ファイル名は学習を実行したときに表示されるRun IDにしたがう。

学習後のパラメータはsrc/parametersに保存される。ファイル名はRun IDにしたがう。

### train_config各種の説明
- hidden_dim: 隠れ層ひとつあたりのユニット数
- hidden_layers: 隠れ層の数(注意:現在はビルドできるのが1層のみ)
- precalculate_bn: trueのときBNと活性化関数を前計算する
- mu: TNNの重みを0とするしきい値。0にするとBNNに相当する

## 回路のビルド
src/config/build_config.jsonでテスト用・ビルド用のパラメータのpathを指定し、線形層をBinary Adder Treeで行うかどうかをparallelizeで指定する。

> sbt run

でMLP.vを生成する。
...