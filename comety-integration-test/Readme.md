# Comety Integration Test
comety-apiのインテグレーションテストを行うためのプロジェクトです

実行するための各種設定はcomety-bomを参照してください

## インテグレーションテストをプロジェクトとして分けている理由

* comety-apiがjarプロジェクトであるためデプロイしてブラウザから確認するのが困難であるため
* 設定すればできなくはないがeclipse上で素直に動作するとは思えない

* 通常のwarプロジェクトを作成する場合はインテグレーションプロジェクトと分ける必要はない

## コンパイル
mvn clean package -P arquillian-managed

# 忘れがち
chromedriverに実行権限を付与する

# パッチ
ShrinkwrapのMavenImporterで文字コードを読み込まないため
CompilerPluginConfigurationを上書きしています

次回リリースで対応される予定
 