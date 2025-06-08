# ベースイメージ
FROM eclipse-temurin:21-jdk AS build

# 作業ディレクトリ作成
WORKDIR /app

# Maven とソースをコピーしてビルド
COPY . .
RUN ./mvnw clean package -DskipTests

# 実行フェーズ
FROM eclipse-temurin:21-jre
WORKDIR /app

# jar ファイルコピー（ファイル名はあなたの jar に合わせて変更）
COPY --from=build /app/target/webapp-0.0.1-SNAPSHOT.jar app.jar

# アプリ実行
ENTRYPOINT ["java", "-jar", "app.jar"]