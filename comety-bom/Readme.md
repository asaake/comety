# Comety BOM

cometyの親プロジェクトです

このプロジェクト継承したプロジェクトを実行するためには以下の設定が必要です

## Arquillianを使用するための設定

#### TOMCAT_HOMEの設定

    環境変数にTOMCAT_HOMEで使用するTomcatのカレントディレクトリを設定する

#### Tomcat Managerの設定
  
	Manager URL: http://localhost:8080/manager/html

#### Tomcat Usersの設定

conf/tomcat-users.xml に以下を追加
  
    <role rolename="manager-gui"/>
    <role rolename="manager-script"/>
    <user password="arquillian" roles="manager-gui,manager-script" username="arquillian"/>
  
#### Eclipse WTP で Manager を動作させる設定
* ServerのプロパティのServer Locations
  * Use Tomcat installation を選択
* または下のほうにあるModulesタブをクリック
  * Add External Web Module をクリック
  * webapps\managerを/managerで登録する
  * Serversプロジェクト以下にあるtomcat-users.xmlを編集する
      
#### Eclipse WTP で JMX を使用する
* ServerのプロパティのGeneral Information のOpen launch configurationをクリック
  * Argumentsタブをクリック
  * VM argumentsの最後に以下を追加
  * `-Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.port=8089`
  
## Eclipseでのプロファイル選択

* 自身のプロジェクトを右クリック
* Propertiesを選択
* Mavenを選択
* Active Maven Profiles のテキストボックスに使用するプロファイル名を設定する
* arquillian-remote または arquillian-managed

## コマンドでのプロファイル選択
mvn clean package -P arquillian-managed

 