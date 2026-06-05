# RECYCLE APOCALYPSE

ガチャを回すたび、今日の資源が明日の希少品になるディストピアサバイバルMOD。
9種類のアイテムをガチャテーブルに捧げ、Minecraft全アイテムからランダムに1つのアイテムを得られる代わりに、支払った素材はそのワールドで二度とドロップしない。

Minecraft Java Edition 26.1.2 / Fabric 向けの MOD 試作版です。

## 導入方法

### 最短導入用URL

- Fabric Loader インストーラー: https://fabricmc.net/use/installer/
- Fabric API `0.149.1+26.1.2`: https://cdn.modrinth.com/data/P7dR8mSH/versions/BLz7ETCw/fabric-api-0.149.1%2B26.1.2.jar
- RECYCLE APOCALYPSE `0.4.4`: https://github.com/raster0x2a/minecraft-mod-recycle-apocalypse/releases/download/v0.4.4/recycle-apocalypse-0.4.4.jar

Fabric Loader を入れた後、Windows クライアントでは PowerShell に以下を貼り付けると `mods` フォルダへ必要な jar を配置できます。

```powershell
$mods = "$env:APPDATA\.minecraft\mods"
New-Item -ItemType Directory -Force $mods | Out-Null
Invoke-WebRequest "https://cdn.modrinth.com/data/P7dR8mSH/versions/BLz7ETCw/fabric-api-0.149.1%2B26.1.2.jar" -OutFile "$mods\fabric-api-0.149.1+26.1.2.jar"
Invoke-WebRequest "https://github.com/raster0x2a/minecraft-mod-recycle-apocalypse/releases/download/v0.4.4/recycle-apocalypse-0.4.4.jar" -OutFile "$mods\recycle-apocalypse-0.4.4.jar"
```

Linux サーバーでは、サーバーディレクトリで以下を実行してください。

```sh
mkdir -p mods
curl -L -o "mods/fabric-api-0.149.1+26.1.2.jar" "https://cdn.modrinth.com/data/P7dR8mSH/versions/BLz7ETCw/fabric-api-0.149.1%2B26.1.2.jar"
curl -L -o "mods/recycle-apocalypse-0.4.4.jar" "https://github.com/raster0x2a/minecraft-mod-recycle-apocalypse/releases/download/v0.4.4/recycle-apocalypse-0.4.4.jar"
```

### 1. Fabric Loader を入れる

Minecraft Java Edition 26.1.2 用の Fabric Loader をインストールしてください。

推奨:

- Minecraft: `26.1.2`
- Fabric Loader: `0.19.2` 以上
- Java: `25`

### 2. Fabric API を入れる

Fabric API 26.1.2 対応版を `mods` フォルダに入れてください。

推奨:

- Fabric API: `0.149.1+26.1.2`

### 3. この MOD の jar を入れる

ビルド済み jar は以下にあります。

```text
build/libs/recycle-apocalypse-0.4.4.jar
```

この jar を Minecraft の `mods` フォルダに入れてください。

Windows の通常例:

```text
%APPDATA%\.minecraft\mods
```

マルチプレイサーバーで使う場合は、サーバー側と参加する各クライアントの両方に、同じ jar と Fabric API を入れてください。

この MOD はガチャテーブルのカスタムブロックとカスタムGUIを追加するため、現在の版ではクライアント側にも導入が必要です。

## 使い方

### ガチャを引く

#### ガチャテーブルで引く

ガチャテーブルを入手します。

```mcfunction
/recycle give_table
```

ガチャテーブルを設置し、右クリックでUIを開きます。

3x3の9スロットすべてに、9種類の別々のアイテムを1個ずつ配置してください。

例:

```text
丸石 x1 | 土 x1     | 原木 x1
砂 x1   | 火打石 x1 | 種 x1
棒 x1   | 羽根 x1   | 石炭 x1
```

`実行` ボタンを押すと、投入した9種類のアイテムを消費してランダムな景品アイテムを9個入手します。

投入した9種類のアイテムは、その回の景品候補から除外されます。

過去にガチャ素材として使用済みになったアイテムは、再度ガチャ素材にできません。

すでに投入済みのアイテムと同じアイテム、または使用済みアイテムは、ガチャテーブルのスロットに置けません。

UIを閉じた場合、未使用の投入アイテムはプレイヤーに返却されます。

### 消滅済みアイテムを確認する

ガチャテーブルの `使用済み` ボタンを押すと、消滅済みアイテムをアイコン一覧で確認できます。

使用済み一覧では、`<` と `>` ボタンでページ移動し、`戻る` ボタンでガチャ画面に戻ります。

コマンドでも確認できます。

```mcfunction
/recycle used
```

ページ指定:

```mcfunction
/recycle used 2
```

## 仕様

- ガチャテーブルでは、9スロットに9種類の別々のアイテムを1個ずつ置いた合計9個がコストです。
- 使用済みアイテムはドロップしないだけでなく、ガチャ素材としても使用できません。
- 使用済みアイテムを結果として作るクラフトは、結果スロットから取得できません。
- 景品は、登録済みの全アイテムからランダムに1種類選ばれ、9個付与されます。
- ガチャ実行者はサーバー全体チャットへ通知されません。
- ガチャで消費されたアイテムIDは、ワールド保存データに永続化されます。
- 使用済みアイテムは、以後そのワールド内で `ItemEntity` として出現しようとした時点で削除されます。
- ガチャ景品の付与だけは削除対象から除外されます。
- ガチャ成功時には効果音とパーティクルが発生します。
- ガチャテーブルはクラフトテーブルと見分けやすいよう、天板にラピスラズリブロック風の青い見た目を使っています。

## 開発者向けビルド

このワークスペースにはローカル開発ツールが `.tools/` に入っています。

- JDK: `25.0.3`
- Gradle: `9.5.1`

環境を読み込む:

```sh
. tools/env.sh
```

ビルド:

```sh
tools/build.sh
```

成功すると、以下に jar が生成されます。

```text
build/libs/recycle-apocalypse-0.4.4.jar
```

## GitHub Releases

`v0.4.4` のようなバージョンタグをpushすると、GitHub ActionsでMODをビルドし、対応するGitHub Releaseへ通常jarを添付します。

`-sources.jar` はRelease添付対象から除外します。

## 26.1 系のビルド注意点

Minecraft 26.1 系は非難読化版のため、従来の Yarn / intermediary / named mappings を使う構成ではありません。

このプロジェクトでは 26.1 系向けに以下の構成にしています。

- Loom plugin: `net.fabricmc.fabric-loom`
- `mappings` 行なし
- `modImplementation` ではなく `implementation`
- `remapJar` ではなく通常の `jar`

## 今後の予定

現在は、ガチャテーブルUIからガチャを実行できます。

`/recycle give_table` は、ワールド資源が消滅してもガチャテーブルを入手不能にしないための導線として残しています。
