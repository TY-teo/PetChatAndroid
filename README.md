# PetChat Android App

这是根据网页demo创建的PetChat安卓应用。

## 功能特点

1. **宠物对话** - 与宠物进行实时对话，支持文字和语音输入
2. **定位追踪** - 实时查看宠物位置，设置电子围栏
3. **宠物动态** - 分享和查看宠物日常动态
4. **宠物管理** - 管理宠物信息、健康记录、成长记录等

## 项目结构

```
PetChatAndroid/
├── app/
│   ├── src/
│   │   └── main/
│   │       ├── java/com/petchat/android/
│   │       │   ├── MainActivity.java
│   │       │   ├── fragments/
│   │       │   │   ├── ChatFragment.java
│   │       │   │   ├── LocationFragment.java
│   │       │   │   ├── SocialFragment.java
│   │       │   │   └── PetFragment.java
│   │       │   ├── adapters/
│   │       │   │   ├── ChatAdapter.java
│   │       │   │   └── MomentAdapter.java
│   │       │   └── models/
│   │       │       ├── Message.java
│   │       │       └── Moment.java
│   │       └── res/
│   │           ├── layout/
│   │           ├── values/
│   │           ├── drawable/
│   │           └── menu/
│   └── build.gradle
├── build.gradle
├── settings.gradle
└── gradle.properties
```

## 构建和运行

1. 使用Android Studio打开项目
2. 同步Gradle依赖
3. 连接Android设备或启动模拟器
4. 运行应用

## 注意事项

- 部分功能（如语音识别、地图显示等）需要实际设备权限和相关服务支持
- 图标资源需要替换为实际的图片文件
- 某些功能目前仅显示Toast提示，需要后续完善实现

## 技术栈

- Java
- Android SDK
- Material Design Components
- RecyclerView
- CardView
- ViewBinding