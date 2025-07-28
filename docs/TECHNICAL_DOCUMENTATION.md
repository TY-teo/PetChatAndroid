# PetChat Android 技术文档

## 目录

1. [项目概述](#1-项目概述)
2. [架构设计文档](#2-架构设计文档)
3. [API文档](#3-api文档)
4. [开发指南](#4-开发指南)
5. [数据库设计文档](#5-数据库设计文档)
6. [部署文档](#6-部署文档)

---

## 1. 项目概述

### 1.1 项目背景和目标

PetChat Android是一款创新的宠物社交与管理应用，旨在为宠物主人提供与宠物互动的全新体验。该应用通过AI技术模拟宠物对话，结合定位追踪、社交分享等功能，帮助用户更好地了解和照顾自己的宠物。

**核心目标：**
- 提供拟人化的宠物对话体验
- 实时追踪宠物位置，保障宠物安全
- 建立宠物社交网络，分享宠物日常
- 完整的宠物健康与成长记录管理

### 1.2 功能特性

#### 1.2.1 核心功能
1. **智能对话系统**
   - AI驱动的宠物对话模拟
   - 支持文字和语音输入
   - 情感识别与表达（开心、饥饿、疲劳等）
   - 上下文理解与连续对话

2. **定位追踪**
   - 实时GPS定位
   - 电子围栏设置
   - 历史轨迹回放
   - 位置异常告警

3. **宠物社交圈**
   - 发布宠物动态
   - 图片/视频分享
   - 点赞评论互动
   - 附近宠物发现

4. **宠物管理**
   - 基本信息管理
   - 健康记录（疫苗、体检、用药）
   - 成长相册
   - 提醒事项

#### 1.2.2 扩展功能（规划中）
- 宠物健康监测（与智能设备联动）
- 宠物训练指导
- 在线宠物医生咨询
- 宠物用品商城

### 1.3 技术栈

#### 1.3.1 当前技术栈
- **开发语言**: Kotlin + Java混合开发
- **最低SDK版本**: API 24 (Android 7.0)
- **目标SDK版本**: API 34 (Android 14)
- **架构模式**: MVVM（Model-View-ViewModel）
- **UI框架**: Material Design Components
- **依赖注入**: 手动依赖注入（计划迁移到Hilt）
- **异步处理**: Kotlin Coroutines + Flow
- **数据库**: Room
- **图片加载**: Glide
- **权限管理**: RxPermissions

#### 1.3.2 计划引入的技术
- **依赖注入**: Hilt
- **网络请求**: Retrofit + OkHttp
- **WebSocket**: Socket.IO
- **推送通知**: Firebase Cloud Messaging
- **性能监控**: Firebase Performance
- **崩溃分析**: Firebase Crashlytics

---

## 2. 架构设计文档

### 2.1 整体架构

项目采用Clean Architecture + MVVM架构模式，实现关注点分离和高内聚低耦合的设计目标。

```
┌─────────────────────────────────────────────────────────┐
│                    Presentation Layer                    │
│  ┌─────────────────────────────────────────────────┐   │
│  │   UI Components (Activity/Fragment/View)         │   │
│  └─────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────┐   │
│  │   ViewModels                                     │   │
│  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
                            ↕
┌─────────────────────────────────────────────────────────┐
│                     Domain Layer                         │
│  ┌─────────────────────────────────────────────────┐   │
│  │   Use Cases                                      │   │
│  └─────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────┐   │
│  │   Domain Models                                  │   │
│  └─────────────────────────────────────────────────┘   │
│  ┌─────────────────────────────────────────────────┐   │
│  │   Repository Interfaces                          │   │
│  └─────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────┘
                            ↕
┌─────────────────────────────────────────────────────────┐
│                      Data Layer                          │
│  ┌─────────────────────────────────────────────────┐   │
│  │   Repository Implementations                     │   │
│  └─────────────────────────────────────────────────┘   │
│  ┌──────────────────┐  ┌───────────────────────┐       │
│  │  Local DataSource │  │  Remote DataSource    │       │
│  │  (Room Database)  │  │  (Network API)        │       │
│  └──────────────────┘  └───────────────────────┘       │
└─────────────────────────────────────────────────────────┘
```

### 2.2 模块划分

#### 2.2.1 当前模块结构
```
app/
├── data/                    # 数据层
│   ├── database/           # 本地数据库
│   │   ├── dao/           # 数据访问对象
│   │   ├── entity/        # 数据库实体
│   │   └── Converters.kt  # 类型转换器
│   └── repository/        # 仓库实现
├── fragments/             # UI层 - Fragment
├── adapters/             # UI层 - 适配器
├── models/               # 领域模型
├── ui/                   # UI层 - ViewModel
└── MainActivity.kt       # 主Activity
```

#### 2.2.2 目标模块化结构
```
PetChatAndroid/
├── app/                          # 应用主模块
├── core/                         # 核心模块
│   ├── common/                  # 公共工具类
│   ├── network/                 # 网络配置
│   ├── database/                # 数据库配置
│   └── ui/                      # UI基础组件
├── feature/                      # 功能模块
│   ├── auth/                    # 认证模块
│   ├── chat/                    # 对话模块
│   ├── location/                # 定位模块
│   ├── social/                  # 社交模块
│   └── pet/                     # 宠物管理模块
├── data/                         # 数据模块
│   └── repository/              # 仓库实现
└── domain/                       # 领域模块
    ├── model/                   # 领域模型
    ├── repository/              # 仓库接口
    └── usecase/                 # 用例
```

### 2.3 数据流设计

#### 2.3.1 单向数据流
```
User Action → View → ViewModel → UseCase → Repository → DataSource
                ↑                                            ↓
                ←──────────── State/LiveData ←───────────────
```

#### 2.3.2 响应式数据流
- **UI层**: 观察ViewModel中的StateFlow/LiveData
- **ViewModel层**: 通过UseCase调用Repository
- **Repository层**: 协调本地和远程数据源
- **数据源层**: 返回Flow进行响应式数据传递

### 2.4 关键设计决策

1. **MVVM选择理由**
   - Android官方推荐架构
   - 良好的生命周期管理
   - 便于单元测试

2. **Room数据库**
   - 编译时SQL验证
   - 与LiveData/Flow无缝集成
   - 支持数据库迁移

3. **Kotlin Coroutines**
   - 简洁的异步代码
   - 结构化并发
   - 与Android生命周期集成

---

## 3. API文档

### 3.1 数据模型定义

#### 3.1.1 核心实体

**Message - 消息实体**
```kotlin
data class Message(
    val id: String = UUID.randomUUID().toString(),
    val content: String,
    val type: MessageType,
    val timestamp: Long = System.currentTimeMillis(),
    val emotion: PetEmotion = PetEmotion.NORMAL,
    val mediaUri: String? = null
)

enum class MessageType {
    USER,    // 用户消息
    PET      // 宠物消息
}

enum class PetEmotion {
    NORMAL,    // 正常
    HAPPY,     // 开心
    SAD,       // 难过
    HUNGRY,    // 饥饿
    TIRED,     // 疲劳
    EXCITED,   // 兴奋
    PLAYFUL,   // 想玩耍
    CURIOUS    // 好奇
}
```

**Pet - 宠物实体**
```kotlin
data class Pet(
    val id: String,
    val name: String,
    val type: PetType,
    val breed: String,
    val gender: Gender,
    val birthDate: Date,
    val weight: Float,
    val avatarUrl: String?,
    val description: String?
)

enum class PetType {
    DOG, CAT, BIRD, RABBIT, OTHER
}

enum class Gender {
    MALE, FEMALE
}
```

**Location - 位置信息**
```kotlin
data class Location(
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val timestamp: Long,
    val address: String? = null
)
```

**Moment - 宠物动态**
```kotlin
data class Moment(
    val id: String,
    val petId: String,
    val content: String,
    val images: List<String>,
    val location: Location?,
    val createTime: Long,
    val likes: Int = 0,
    val comments: List<Comment> = emptyList()
)
```

### 3.2 Repository接口

#### 3.2.1 ChatRepository
```kotlin
interface ChatRepository {
    // 获取消息列表
    fun getMessages(chatId: String): Flow<List<Message>>
    
    // 发送消息
    suspend fun sendMessage(message: Message): Result<Message>
    
    // 删除消息
    suspend fun deleteMessage(messageId: String): Result<Unit>
    
    // 清空聊天记录
    suspend fun clearChat(chatId: String): Result<Unit>
    
    // 获取宠物情绪状态
    fun getPetEmotion(petId: String): Flow<PetEmotion>
}
```

#### 3.2.2 PetRepository
```kotlin
interface PetRepository {
    // 获取宠物列表
    fun getPets(): Flow<List<Pet>>
    
    // 获取单个宠物信息
    suspend fun getPet(petId: String): Result<Pet>
    
    // 添加宠物
    suspend fun addPet(pet: Pet): Result<Pet>
    
    // 更新宠物信息
    suspend fun updatePet(pet: Pet): Result<Pet>
    
    // 删除宠物
    suspend fun deletePet(petId: String): Result<Unit>
    
    // 上传宠物头像
    suspend fun uploadAvatar(petId: String, imageUri: Uri): Result<String>
}
```

#### 3.2.3 LocationRepository
```kotlin
interface LocationRepository {
    // 获取当前位置
    suspend fun getCurrentLocation(): Result<Location>
    
    // 获取宠物实时位置
    fun getPetLocation(petId: String): Flow<Location>
    
    // 获取历史轨迹
    suspend fun getLocationHistory(
        petId: String,
        startTime: Long,
        endTime: Long
    ): Result<List<Location>>
    
    // 设置电子围栏
    suspend fun setGeofence(
        petId: String,
        center: Location,
        radius: Float
    ): Result<Unit>
}
```

### 3.3 ViewModel公共接口

#### 3.3.1 ChatViewModel
```kotlin
class ChatViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {
    
    // UI状态
    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
    
    // 消息列表
    val messages: Flow<List<Message>> = chatRepository.getMessages(currentChatId)
    
    // 发送消息
    fun sendMessage(content: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSending = true) }
            
            chatRepository.sendMessage(
                Message(
                    content = content,
                    type = MessageType.USER
                )
            ).fold(
                onSuccess = { 
                    _uiState.update { it.copy(isSending = false) }
                },
                onFailure = { error ->
                    _uiState.update { 
                        it.copy(
                            isSending = false,
                            error = error.message
                        )
                    }
                }
            )
        }
    }
    
    // 清空聊天
    fun clearChat() {
        viewModelScope.launch {
            chatRepository.clearChat(currentChatId)
        }
    }
}

data class ChatUiState(
    val isSending: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null
)
```

---

## 4. 开发指南

### 4.1 环境配置

#### 4.1.1 开发环境要求
- **Android Studio**: Arctic Fox (2020.3.1) 或更高版本
- **JDK**: 11 或更高版本
- **Gradle**: 7.5 或更高版本
- **Kotlin**: 1.9.0 或更高版本

#### 4.1.2 环境设置步骤
1. 安装Android Studio
2. 配置Android SDK（确保安装API 24-34）
3. 配置模拟器或连接真实设备
4. 克隆项目代码
   ```bash
   git clone https://github.com/your-org/PetChatAndroid.git
   ```

### 4.2 构建和运行

#### 4.2.1 构建配置
```gradle
// app/build.gradle
android {
    compileSdk 34
    
    defaultConfig {
        applicationId "com.petchat.android"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0.0"
    }
    
    buildTypes {
        debug {
            debuggable true
            minifyEnabled false
            applicationIdSuffix ".debug"
        }
        release {
            debuggable false
            minifyEnabled true
            shrinkResources true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 
                         'proguard-rules.pro'
        }
    }
}
```

#### 4.2.2 运行应用
```bash
# Debug版本
./gradlew assembleDebug
./gradlew installDebug

# Release版本
./gradlew assembleRelease
```

### 4.3 代码规范

#### 4.3.1 Kotlin代码规范
1. **命名规范**
   - 类名：PascalCase（如 `ChatViewModel`）
   - 函数名：camelCase（如 `sendMessage`）
   - 常量：UPPER_SNAKE_CASE（如 `MAX_MESSAGE_LENGTH`）
   - 包名：全小写（如 `com.petchat.android`）

2. **代码格式**
   - 使用4个空格缩进
   - 每行最多100个字符
   - 使用尾随逗号

3. **最佳实践**
   ```kotlin
   // 使用数据类
   data class User(val id: String, val name: String)
   
   // 使用扩展函数
   fun String.isValidEmail(): Boolean {
       return this.matches(Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"))
   }
   
   // 使用密封类处理状态
   sealed class UiState {
       object Loading : UiState()
       data class Success(val data: List<Item>) : UiState()
       data class Error(val message: String) : UiState()
   }
   ```

#### 4.3.2 资源命名规范
- **布局文件**: `<类型>_<描述>.xml`
  - `activity_main.xml`
  - `fragment_chat.xml`
  - `item_message.xml`
  
- **Drawable资源**: `<类型>_<描述>_<状态>.xml`
  - `ic_send_message.xml`
  - `bg_button_primary.xml`
  - `selector_tab_icon.xml`

- **字符串资源**: `<界面>_<描述>`
  - `chat_hint_input`
  - `error_network_unavailable`

### 4.4 Git工作流程

#### 4.4.1 分支策略
```
main
  ├── develop
  │     ├── feature/chat-optimization
  │     ├── feature/location-tracking
  │     └── feature/social-sharing
  ├── release/1.0.0
  └── hotfix/crash-fix
```

#### 4.4.2 提交规范
```
<type>(<scope>): <subject>

<body>

<footer>
```

**类型（type）**:
- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 重构
- `test`: 测试相关
- `chore`: 构建过程或辅助工具的变动

**示例**:
```
feat(chat): 添加语音消息支持

- 集成语音录制功能
- 支持语音消息的发送和播放
- 添加语音消息的UI展示

Closes #123
```

#### 4.4.3 代码审查要点
1. 代码是否符合项目规范
2. 是否有适当的注释和文档
3. 是否考虑了边界情况
4. 是否有性能问题
5. 是否有安全隐患
6. 测试覆盖是否充分

---

## 5. 数据库设计文档

### 5.1 表结构设计

#### 5.1.1 消息表（messages）
```sql
CREATE TABLE messages (
    id TEXT PRIMARY KEY,
    chat_id TEXT NOT NULL,
    content TEXT NOT NULL,
    type INTEGER NOT NULL,
    emotion INTEGER DEFAULT 0,
    media_uri TEXT,
    timestamp INTEGER NOT NULL,
    is_read INTEGER DEFAULT 0,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL
);

CREATE INDEX idx_messages_chat_id ON messages(chat_id);
CREATE INDEX idx_messages_timestamp ON messages(timestamp);
```

#### 5.1.2 宠物表（pets）
```sql
CREATE TABLE pets (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    type INTEGER NOT NULL,
    breed TEXT,
    gender INTEGER NOT NULL,
    birth_date INTEGER NOT NULL,
    weight REAL,
    avatar_url TEXT,
    description TEXT,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL
);
```

#### 5.1.3 位置记录表（locations）
```sql
CREATE TABLE locations (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    pet_id TEXT NOT NULL,
    latitude REAL NOT NULL,
    longitude REAL NOT NULL,
    accuracy REAL,
    address TEXT,
    timestamp INTEGER NOT NULL,
    created_at INTEGER NOT NULL,
    FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE
);

CREATE INDEX idx_locations_pet_id ON locations(pet_id);
CREATE INDEX idx_locations_timestamp ON locations(timestamp);
```

#### 5.1.4 动态表（moments）
```sql
CREATE TABLE moments (
    id TEXT PRIMARY KEY,
    pet_id TEXT NOT NULL,
    content TEXT NOT NULL,
    images TEXT, -- JSON array
    location_lat REAL,
    location_lng REAL,
    likes INTEGER DEFAULT 0,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL,
    FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE
);
```

### 5.2 关系图

```
┌─────────────┐     1:N     ┌─────────────┐
│    pets     │ ←───────────│  messages   │
└─────────────┘             └─────────────┘
       │                           
       │ 1:N                       
       ↓                           
┌─────────────┐                    
│  locations  │                    
└─────────────┘                    
       │                           
       │ 1:N                       
       ↓                           
┌─────────────┐                    
│   moments   │                    
└─────────────┘                    
```

### 5.3 数据迁移策略

#### 5.3.1 版本管理
```kotlin
@Database(
    entities = [MessageEntity::class, PetEntity::class],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2)
    ]
)
abstract class PetChatDatabase : RoomDatabase() {
    // ...
}
```

#### 5.3.2 手动迁移示例
```kotlin
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        // 添加新字段
        database.execSQL("ALTER TABLE messages ADD COLUMN is_read INTEGER DEFAULT 0")
        
        // 创建新表
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS health_records (
                id TEXT PRIMARY KEY,
                pet_id TEXT NOT NULL,
                type TEXT NOT NULL,
                description TEXT,
                date INTEGER NOT NULL,
                FOREIGN KEY (pet_id) REFERENCES pets(id) ON DELETE CASCADE
            )
        """)
    }
}
```

#### 5.3.3 数据备份策略
1. **自动备份**: 使用Android Auto Backup
2. **手动导出**: 提供数据导出功能
3. **云同步**: 计划集成云存储服务

---

## 6. 部署文档

### 6.1 打包配置

#### 6.1.1 构建变体配置
```gradle
android {
    flavorDimensions "environment"
    productFlavors {
        dev {
            dimension "environment"
            applicationIdSuffix ".dev"
            versionNameSuffix "-dev"
            buildConfigField "String", "API_BASE_URL", "\"https://dev-api.petchat.com\""
        }
        staging {
            dimension "environment"
            applicationIdSuffix ".staging"
            versionNameSuffix "-staging"
            buildConfigField "String", "API_BASE_URL", "\"https://staging-api.petchat.com\""
        }
        production {
            dimension "environment"
            buildConfigField "String", "API_BASE_URL", "\"https://api.petchat.com\""
        }
    }
}
```

#### 6.1.2 混淆配置
```proguard
# 保留数据模型
-keep class com.petchat.android.models.** { *; }
-keep class com.petchat.android.data.database.entity.** { *; }

# 保留枚举
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Kotlin
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.**
-keepclassmembers class **$WhenMappings {
    <fields>;
}
```

### 6.2 签名流程

#### 6.2.1 生成签名密钥
```bash
keytool -genkey -v -keystore petchat-release.keystore \
    -alias petchat -keyalg RSA -keysize 2048 -validity 10000
```

#### 6.2.2 配置签名
```gradle
android {
    signingConfigs {
        release {
            storeFile file(RELEASE_STORE_FILE)
            storePassword RELEASE_STORE_PASSWORD
            keyAlias RELEASE_KEY_ALIAS
            keyPassword RELEASE_KEY_PASSWORD
        }
    }
    
    buildTypes {
        release {
            signingConfig signingConfigs.release
        }
    }
}
```

#### 6.2.3 安全存储密钥
在项目根目录创建 `keystore.properties`:
```properties
storePassword=your_store_password
keyPassword=your_key_password
keyAlias=petchat
storeFile=../keystore/petchat-release.keystore
```

### 6.3 发布流程

#### 6.3.1 版本号管理
```gradle
def versionMajor = 1
def versionMinor = 0
def versionPatch = 0
def versionBuild = 1 // CI构建号

android {
    defaultConfig {
        versionCode versionMajor * 10000 + versionMinor * 100 + versionPatch * 10 + versionBuild
        versionName "${versionMajor}.${versionMinor}.${versionPatch}"
    }
}
```

#### 6.3.2 构建发布版本
```bash
# 清理之前的构建
./gradlew clean

# 构建Release APK
./gradlew assembleProductionRelease

# 构建App Bundle
./gradlew bundleProductionRelease
```

#### 6.3.3 Google Play发布流程
1. **准备材料**
   - 应用图标（512x512）
   - 功能图形（1024x500）
   - 屏幕截图（手机、平板）
   - 应用描述（多语言）
   - 隐私政策链接

2. **发布步骤**
   - 登录Google Play Console
   - 创建应用/选择现有应用
   - 上传App Bundle
   - 填写商店信息
   - 设置内容分级
   - 定价和分发
   - 提交审核

3. **分阶段发布**
   ```
   5% → 10% → 25% → 50% → 100%
   ```

#### 6.3.4 应用商店优化（ASO）
1. **关键词优化**
   - 标题：PetChat - 智能宠物对话与管理
   - 关键词：宠物、对话、定位、社交、管理

2. **描述优化**
   - 突出核心功能
   - 使用列表格式
   - 包含用户评价

3. **视觉优化**
   - 高质量截图
   - 功能演示视频
   - 吸引人的图标

### 6.4 监控和维护

#### 6.4.1 崩溃监控
```kotlin
// Application类中初始化
class PetChatApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Firebase Crashlytics
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)
    }
}
```

#### 6.4.2 性能监控
```kotlin
// 关键操作性能追踪
val trace = Firebase.performance.newTrace("send_message")
trace.start()
// 执行操作
sendMessage()
trace.stop()
```

#### 6.4.3 用户反馈收集
1. **应用内反馈**
2. **应用商店评价监控**
3. **社交媒体监听**
4. **定期用户调研**

---

## 附录

### A. 常见问题解决

1. **构建失败**
   - 清理缓存：`./gradlew clean`
   - 同步项目：File → Sync Project with Gradle Files
   - 检查依赖版本冲突

2. **运行时崩溃**
   - 检查权限申请
   - 验证ProGuard规则
   - 查看Logcat日志

### B. 相关资源

- [Android开发者文档](https://developer.android.com)
- [Material Design指南](https://material.io/design)
- [Kotlin官方文档](https://kotlinlang.org/docs)
- [项目Wiki](https://github.com/your-org/PetChatAndroid/wiki)

### C. 联系方式

- 技术支持：tech@petchat.com
- 产品反馈：feedback@petchat.com
- 紧急问题：emergency@petchat.com

---

*文档版本：1.0.0*  
*最后更新：2025-01-27*  
*下次审核：2025-02-27*