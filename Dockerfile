# ベースイメージ（ビルドフェーズ）
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Maven ラッパーと設定ファイルを先にコピー（依存キャッシュを効かせるため）
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# 依存関係を先に解決（ここまでキャッシュされる）
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# アプリケーションのソースコードを後でコピー
COPY src ./src

# クリーンなしでビルド（無駄な再ビルド回避）
RUN ./mvnw package -DskipTests

# 実行フェーズ
FROM eclipse-temurin:21-jre
WORKDIR /app

# jar をコピー
COPY --from=build /app/target/webapp-0.0.1-SNAPSHOT.jar app.jar

# 実行
ENTRYPOINT ["java", "-jar", "app.jar"]