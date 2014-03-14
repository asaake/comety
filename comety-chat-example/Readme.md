# Comety-Chat-Example
comety-apiを使用して作成したチャットのサンプルプロジェクトです

各種設定についてはcomety-bomを参照してください

デプロイして下記にアクセスしてください。  
[http://localhost:8080/comety-chat-example/jsp/chat.jsp](http://localhost:8080/comety-chat-example/jsp/chat.jsp)

# プロファイル
test, arquillian-remote, arquillian-managedが使用可能です  
eclipseで開発する際はprofileの設定にtestを含めてください

# コンパイル
mvn clean package -P arquillian-managed

# テストの実行のみキャンセル
mvn clean package -P arquillian-managed -DskipTests=true

# パッチ
ShrinkwrapのMavenImporterで文字コードを読み込まないため
CompilerPluginConfigurationを上書きしています

次回リリースで対応される予定
