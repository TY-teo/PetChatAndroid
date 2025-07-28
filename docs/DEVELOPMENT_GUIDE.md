# PetChat Android 开发规范与最佳实践

## 目录

1. [项目结构规范](#1-项目结构规范)
2. [代码规范](#2-代码规范)
3. [架构规范](#3-架构规范)
4. [UI/UX规范](#4-uiux规范)
5. [测试规范](#5-测试规范)
6. [性能优化](#6-性能优化)
7. [安全规范](#7-安全规范)
8. [Git工作流](#8-git工作流)
9. [持续集成](#9-持续集成)
10. [故障排查](#10-故障排查)

---

## 1. 项目结构规范

### 1.1 模块化结构
```
PetChatAndroid/
├── app/                              # 应用主模块
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/petchat/android/
│   │   │   │   ├── di/              # 依赖注入
│   │   │   │   ├── ui/              # UI层
│   │   │   │   │   ├── base/       # 基类
│   │   │   │   │   ├── chat/       # 聊天功能
│   │   │   │   │   ├── pet/        # 宠物管理
│   │   │   │   │   ├── location/   # 定位功能
│   │   │   │   │   └── social/     # 社交功能
│   │   │   │   ├── data/            # 数据层
│   │   │   │   │   ├── local/      # 本地数据源
│   │   │   │   │   ├── remote/     # 远程数据源
│   │   │   │   │   └── repository/ # 仓库实现
│   │   │   │   ├── domain/          # 领域层
│   │   │   │   │   ├── model/      # 领域模型
│   │   │   │   │   ├── repository/ # 仓库接口
│   │   │   │   │   └── usecase/    # 用例
│   │   │   │   ├── util/            # 工具类
│   │   │   │   └── PetChatApp.kt   # Application类
│   │   │   └── res/                 # 资源文件
│   │   ├── test/                    # 单元测试
│   │   └── androidTest/             # 仪器测试
│   └── build.gradle.kts
├── buildSrc/                         # 构建脚本
│   └── src/main/kotlin/
│       ├── Dependencies.kt          # 依赖版本管理
│       └── AppConfig.kt            # 应用配置
├── docs/                            # 文档
├── scripts/                         # 脚本
└── gradle.properties
```

### 1.2 包命名规范
```kotlin
// 功能模块包结构
com.petchat.android.ui.chat
    ├── ChatFragment.kt
    ├── ChatViewModel.kt
    ├── ChatAdapter.kt
    └── ChatUiState.kt

// 数据层包结构
com.petchat.android.data
    ├── local
    │   ├── dao
    │   ├── entity
    │   └── database
    ├── remote
    │   ├── api
    │   ├── dto
    │   └── service
    └── repository
```

---

## 2. 代码规范

### 2.1 Kotlin编码规范

#### 2.1.1 命名规范
```kotlin
// 类名：PascalCase
class UserProfileViewModel : ViewModel()

// 函数名：camelCase
fun calculateDistance(lat1: Double, lng1: Double): Float

// 常量：UPPER_SNAKE_CASE
companion object {
    private const val MAX_RETRY_COUNT = 3
    private const val TIMEOUT_SECONDS = 30L
}

// 变量：camelCase
private val userRepository: UserRepository
var isLoading = false

// 包名：全小写
package com.petchat.android.data.repository
```

#### 2.1.2 代码格式化
```kotlin
// 函数参数换行
fun sendMessage(
    chatId: String,
    content: String,
    mediaUri: Uri? = null,
    callback: (Result<Message>) -> Unit
) {
    // 实现
}

// 链式调用
viewModel.userState
    .flowWithLifecycle(lifecycle)
    .onEach { state ->
        updateUi(state)
    }
    .launchIn(lifecycleScope)

// Lambda表达式
users.filter { it.isActive }
    .map { it.name }
    .forEach { name ->
        println(name)
    }
```

#### 2.1.3 注释规范
```kotlin
/**
 * 发送消息到指定聊天
 *
 * @param chatId 聊天ID
 * @param content 消息内容
 * @param mediaUri 媒体文件URI（可选）
 * @return 发送结果
 */
suspend fun sendMessage(
    chatId: String,
    content: String,
    mediaUri: Uri? = null
): Result<Message> {
    // TODO: 实现媒体文件上传
    // FIXME: 处理网络异常情况
    
    // 验证消息内容
    if (content.isBlank()) {
        return Result.failure(IllegalArgumentException("消息内容不能为空"))
    }
    
    // 发送消息
    return repository.sendMessage(chatId, content, mediaUri)
}
```

### 2.2 资源命名规范

#### 2.2.1 布局文件
```
activity_<功能>.xml      // activity_main.xml
fragment_<功能>.xml      // fragment_chat.xml
dialog_<功能>.xml        // dialog_confirm.xml
item_<列表名>_<类型>.xml  // item_message_user.xml
view_<自定义view>.xml    // view_pet_avatar.xml
```

#### 2.2.2 Drawable资源
```
ic_<功能>_<描述>.xml     // ic_send_message.xml
bg_<描述>.xml           // bg_button_primary.xml
selector_<描述>.xml     // selector_tab_icon.xml
shape_<描述>.xml        // shape_rounded_corner.xml
```

#### 2.2.3 字符串资源
```xml
<!-- strings.xml -->
<resources>
    <!-- 通用 -->
    <string name="app_name">PetChat</string>
    <string name="common_ok">确定</string>
    <string name="common_cancel">取消</string>
    
    <!-- 功能模块 -->
    <string name="chat_title">对话</string>
    <string name="chat_hint_input">输入消息...</string>
    <string name="chat_error_send_failed">发送失败，请重试</string>
    
    <!-- 格式化字符串 -->
    <string name="pet_age_format">%1$d岁%2$d个月</string>
</resources>
```

### 2.3 最佳实践

#### 2.3.1 使用扩展函数
```kotlin
// View扩展
fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

// Context扩展
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

// String扩展
fun String.isValidPhone(): Boolean {
    return matches(Regex("^1[3-9]\\d{9}$"))
}
```

#### 2.3.2 使用密封类处理状态
```kotlin
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val exception: Throwable) : UiState<Nothing>()
}

// 使用
when (uiState) {
    is UiState.Loading -> showLoading()
    is UiState.Success -> showData(uiState.data)
    is UiState.Error -> showError(uiState.exception)
}
```

#### 2.3.3 使用协程处理异步
```kotlin
class ChatViewModel(
    private val repository: ChatRepository
) : ViewModel() {
    
    fun sendMessage(content: String) {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            
            repository.sendMessage(content)
                .flowOn(Dispatchers.IO)
                .catch { e ->
                    _uiState.value = UiState.Error(e)
                }
                .collect { message ->
                    _uiState.value = UiState.Success(message)
                }
        }
    }
}
```

---

## 3. 架构规范

### 3.1 MVVM架构实现

#### 3.1.1 View层（Activity/Fragment）
```kotlin
class ChatFragment : Fragment() {
    
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var binding: FragmentChatBinding
    private lateinit var adapter: ChatAdapter
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupUi()
        observeViewModel()
    }
    
    private fun setupUi() {
        // 初始化UI组件
        adapter = ChatAdapter()
        binding.recyclerView.adapter = adapter
        
        binding.sendButton.setOnClickListener {
            val message = binding.inputEditText.text.toString()
            if (message.isNotBlank()) {
                viewModel.sendMessage(message)
                binding.inputEditText.text.clear()
            }
        }
    }
    
    private fun observeViewModel() {
        // 观察UI状态
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    when (state) {
                        is ChatUiState.Loading -> showLoading()
                        is ChatUiState.Success -> showMessages(state.messages)
                        is ChatUiState.Error -> showError(state.message)
                    }
                }
            }
        }
    }
}
```

#### 3.1.2 ViewModel层
```kotlin
@HiltViewModel
class ChatViewModel @Inject constructor(
    private val sendMessageUseCase: SendMessageUseCase,
    private val getMessagesUseCase: GetMessagesUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val chatId = savedStateHandle.get<String>("chatId") ?: ""
    
    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Loading)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()
    
    init {
        loadMessages()
    }
    
    private fun loadMessages() {
        viewModelScope.launch {
            getMessagesUseCase(chatId)
                .catch { e ->
                    _uiState.value = ChatUiState.Error(e.message ?: "Unknown error")
                }
                .collect { messages ->
                    _uiState.value = ChatUiState.Success(messages)
                }
        }
    }
    
    fun sendMessage(content: String) {
        viewModelScope.launch {
            sendMessageUseCase(chatId, content)
                .onSuccess { 
                    // 消息发送成功，列表会通过Flow自动更新
                }
                .onFailure { e ->
                    _uiState.value = ChatUiState.Error(e.message ?: "Send failed")
                }
        }
    }
}

sealed class ChatUiState {
    object Loading : ChatUiState()
    data class Success(val messages: List<Message>) : ChatUiState()
    data class Error(val message: String) : ChatUiState()
}
```

#### 3.1.3 Repository层
```kotlin
interface ChatRepository {
    fun getMessages(chatId: String): Flow<List<Message>>
    suspend fun sendMessage(chatId: String, content: String): Result<Message>
}

class ChatRepositoryImpl @Inject constructor(
    private val localDataSource: ChatLocalDataSource,
    private val remoteDataSource: ChatRemoteDataSource,
    private val networkChecker: NetworkChecker
) : ChatRepository {
    
    override fun getMessages(chatId: String): Flow<List<Message>> = flow {
        // 先发送本地数据
        emit(localDataSource.getMessages(chatId))
        
        // 如果有网络，同步远程数据
        if (networkChecker.isConnected()) {
            try {
                val remoteMessages = remoteDataSource.getMessages(chatId)
                localDataSource.insertMessages(remoteMessages)
                emit(localDataSource.getMessages(chatId))
            } catch (e: Exception) {
                // 网络错误不影响本地数据显示
            }
        }
    }.flowOn(Dispatchers.IO)
    
    override suspend fun sendMessage(
        chatId: String, 
        content: String
    ): Result<Message> = withContext(Dispatchers.IO) {
        try {
            val message = Message(
                chatId = chatId,
                content = content,
                timestamp = System.currentTimeMillis(),
                status = MessageStatus.SENDING
            )
            
            // 先保存到本地
            localDataSource.insertMessage(message)
            
            // 发送到服务器
            val response = remoteDataSource.sendMessage(message)
            
            // 更新本地状态
            localDataSource.updateMessageStatus(message.id, MessageStatus.SENT)
            
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
```

### 3.2 依赖注入（Hilt）

#### 3.2.1 Application配置
```kotlin
@HiltAndroidApp
class PetChatApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // 初始化第三方库
        initializeLibraries()
    }
    
    private fun initializeLibraries() {
        // Timber日志
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        // 其他初始化...
    }
}
```

#### 3.2.2 模块配置
```kotlin
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PetChatDatabase {
        return Room.databaseBuilder(
            context,
            PetChatDatabase::class.java,
            "petchat_database"
        ).build()
    }
    
    @Provides
    fun provideMessageDao(database: PetChatDatabase): MessageDao {
        return database.messageDao()
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    abstract fun bindChatRepository(
        impl: ChatRepositoryImpl
    ): ChatRepository
}
```

---

## 4. UI/UX规范

### 4.1 Material Design规范

#### 4.1.1 主题配置
```xml
<!-- themes.xml -->
<resources>
    <style name="Theme.PetChat" parent="Theme.Material3.Light.NoActionBar">
        <!-- Primary brand color -->
        <item name="colorPrimary">@color/pink_500</item>
        <item name="colorPrimaryVariant">@color/pink_700</item>
        <item name="colorOnPrimary">@color/white</item>
        
        <!-- Secondary brand color -->
        <item name="colorSecondary">@color/blue_500</item>
        <item name="colorSecondaryVariant">@color/blue_700</item>
        <item name="colorOnSecondary">@color/white</item>
        
        <!-- Status bar color -->
        <item name="android:statusBarColor">@color/white</item>
        <item name="android:windowLightStatusBar">true</item>
    </style>
</resources>
```

#### 4.1.2 颜色规范
```xml
<!-- colors.xml -->
<resources>
    <!-- 主色调 -->
    <color name="pink_50">#FCE4EC</color>
    <color name="pink_100">#F8BBD0</color>
    <color name="pink_200">#F48FB1</color>
    <color name="pink_300">#F06292</color>
    <color name="pink_400">#EC407A</color>
    <color name="pink_500">#E91E63</color>
    <color name="pink_600">#D81B60</color>
    <color name="pink_700">#C2185B</color>
    
    <!-- 辅助色 -->
    <color name="blue_500">#2196F3</color>
    <color name="green_500">#4CAF50</color>
    <color name="orange_500">#FF9800</color>
    <color name="red_500">#F44336</color>
    
    <!-- 中性色 -->
    <color name="gray_50">#FAFAFA</color>
    <color name="gray_100">#F5F5F5</color>
    <color name="gray_200">#EEEEEE</color>
    <color name="gray_300">#E0E0E0</color>
    <color name="gray_400">#BDBDBD</color>
    <color name="gray_500">#9E9E9E</color>
    <color name="gray_600">#757575</color>
    <color name="gray_700">#616161</color>
    <color name="gray_800">#424242</color>
    <color name="gray_900">#212121</color>
</resources>
```

#### 4.1.3 尺寸规范
```xml
<!-- dimens.xml -->
<resources>
    <!-- 间距 -->
    <dimen name="spacing_tiny">4dp</dimen>
    <dimen name="spacing_small">8dp</dimen>
    <dimen name="spacing_medium">16dp</dimen>
    <dimen name="spacing_large">24dp</dimen>
    <dimen name="spacing_xlarge">32dp</dimen>
    
    <!-- 文字大小 -->
    <dimen name="text_size_caption">12sp</dimen>
    <dimen name="text_size_body">14sp</dimen>
    <dimen name="text_size_subtitle">16sp</dimen>
    <dimen name="text_size_title">20sp</dimen>
    <dimen name="text_size_headline">24sp</dimen>
    
    <!-- 圆角 -->
    <dimen name="corner_radius_small">4dp</dimen>
    <dimen name="corner_radius_medium">8dp</dimen>
    <dimen name="corner_radius_large">16dp</dimen>
    
    <!-- 图标 -->
    <dimen name="icon_size_small">24dp</dimen>
    <dimen name="icon_size_medium">36dp</dimen>
    <dimen name="icon_size_large">48dp</dimen>
</resources>
```

### 4.2 动画规范

#### 4.2.1 过渡动画
```kotlin
// Fragment过渡动画
class ChatFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val transition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
        enterTransition = transition
        returnTransition = transition
    }
}

// 共享元素动画
ViewCompat.setTransitionName(imageView, "pet_avatar_${pet.id}")

val extras = FragmentNavigatorExtras(
    imageView to "pet_avatar_${pet.id}"
)

findNavController().navigate(
    R.id.action_to_detail,
    bundleOf("petId" to pet.id),
    null,
    extras
)
```

#### 4.2.2 加载动画
```kotlin
// Shimmer loading效果
<com.facebook.shimmer.ShimmerFrameLayout
    android:id="@+id/shimmerLayout"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">
    
    <include layout="@layout/placeholder_item_message" />
    
</com.facebook.shimmer.ShimmerFrameLayout>

// 代码中控制
binding.shimmerLayout.startShimmer()
binding.shimmerLayout.stopShimmer()
```

### 4.3 响应式设计

#### 4.3.1 多屏幕适配
```xml
<!-- 手机布局: layout/fragment_chat.xml -->
<LinearLayout
    android:orientation="vertical">
    <!-- 内容 -->
</LinearLayout>

<!-- 平板布局: layout-sw600dp/fragment_chat.xml -->
<LinearLayout
    android:orientation="horizontal">
    <!-- 左侧列表 -->
    <!-- 右侧详情 -->
</LinearLayout>
```

#### 4.3.2 暗黑模式
```xml
<!-- values-night/colors.xml -->
<resources>
    <color name="background">@color/gray_900</color>
    <color name="surface">@color/gray_800</color>
    <color name="on_background">@color/gray_50</color>
    <color name="on_surface">@color/gray_100</color>
</resources>
```

---

## 5. 测试规范

### 5.1 单元测试

#### 5.1.1 ViewModel测试
```kotlin
@ExperimentalCoroutinesApi
class ChatViewModelTest {
    
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    
    @MockK
    private lateinit var sendMessageUseCase: SendMessageUseCase
    
    @MockK
    private lateinit var getMessagesUseCase: GetMessagesUseCase
    
    private lateinit var viewModel: ChatViewModel
    
    @Before
    fun setup() {
        MockKAnnotations.init(this)
        viewModel = ChatViewModel(sendMessageUseCase, getMessagesUseCase)
    }
    
    @Test
    fun `sendMessage should update ui state to success`() = runTest {
        // Given
        val message = Message(content = "Hello")
        coEvery { sendMessageUseCase(any(), any()) } returns Result.success(message)
        
        // When
        viewModel.sendMessage("Hello")
        
        // Then
        assertEquals(ChatUiState.Success, viewModel.uiState.value)
        coVerify { sendMessageUseCase(any(), "Hello") }
    }
}
```

#### 5.1.2 Repository测试
```kotlin
class ChatRepositoryTest {
    
    @MockK
    private lateinit var localDataSource: ChatLocalDataSource
    
    @MockK
    private lateinit var remoteDataSource: ChatRemoteDataSource
    
    private lateinit var repository: ChatRepository
    
    @Before
    fun setup() {
        MockKAnnotations.init(this)
        repository = ChatRepositoryImpl(localDataSource, remoteDataSource)
    }
    
    @Test
    fun `getMessages should emit local then remote data`() = runTest {
        // Given
        val localMessages = listOf(Message(content = "Local"))
        val remoteMessages = listOf(Message(content = "Remote"))
        
        coEvery { localDataSource.getMessages(any()) } returns localMessages
        coEvery { remoteDataSource.getMessages(any()) } returns remoteMessages
        
        // When
        val emissions = repository.getMessages("chat1").take(2).toList()
        
        // Then
        assertEquals(localMessages, emissions[0])
        assertEquals(remoteMessages, emissions[1])
    }
}
```

### 5.2 UI测试

#### 5.2.1 Fragment测试
```kotlin
@RunWith(AndroidJUnit4::class)
class ChatFragmentTest {
    
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)
    
    @Test
    fun testSendMessage() {
        // 导航到聊天页面
        onView(withId(R.id.navigation_chat)).perform(click())
        
        // 输入消息
        onView(withId(R.id.inputEditText))
            .perform(typeText("Hello Pet"))
            .perform(closeSoftKeyboard())
        
        // 点击发送
        onView(withId(R.id.sendButton)).perform(click())
        
        // 验证消息显示
        onView(withText("Hello Pet"))
            .check(matches(isDisplayed()))
    }
}
```

### 5.3 测试覆盖率
```gradle
android {
    buildTypes {
        debug {
            testCoverageEnabled true
        }
    }
}

tasks.register<JacocoReport>("jacocoTestReport") {
    dependsOn("testDebugUnitTest")
    
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    
    sourceDirectories.setFrom(files("src/main/java"))
    classDirectories.setFrom(files("build/tmp/kotlin-classes/debug"))
    executionData.setFrom(files("build/jacoco/testDebugUnitTest.exec"))
}
```

---

## 6. 性能优化

### 6.1 启动优化

#### 6.1.1 冷启动优化
```kotlin
// 使用启动画面
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // 不要在这里setContentView
        super.onCreate(savedInstanceState)
        
        // 异步初始化
        lifecycleScope.launch {
            initializeApp()
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
        }
    }
    
    private suspend fun initializeApp() = withContext(Dispatchers.IO) {
        // 初始化必要的库
    }
}
```

#### 6.1.2 懒加载
```kotlin
// Fragment懒加载
class PetFragment : Fragment() {
    private val viewModel: PetViewModel by viewModels()
    
    override fun onResume() {
        super.onResume()
        // 只在可见时加载数据
        if (isVisible) {
            viewModel.loadPets()
        }
    }
}

// ViewModel懒加载
class MainActivity : AppCompatActivity() {
    // 只在需要时创建ViewModel
    private val chatViewModel: ChatViewModel by viewModels()
}
```

### 6.2 内存优化

#### 6.2.1 图片加载优化
```kotlin
// 使用Glide优化图片加载
Glide.with(context)
    .load(imageUrl)
    .diskCacheStrategy(DiskCacheStrategy.ALL)
    .placeholder(R.drawable.placeholder)
    .error(R.drawable.error)
    .thumbnail(0.1f) // 缩略图
    .override(Target.SIZE_ORIGINAL) // 原始尺寸
    .into(imageView)

// RecyclerView中优化
override fun onViewRecycled(holder: ViewHolder) {
    super.onViewRecycled(holder)
    Glide.with(context).clear(holder.imageView)
}
```

#### 6.2.2 内存泄漏预防
```kotlin
class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    
    // 避免内存泄漏的Handler
    private val handler = Handler(Looper.getMainLooper())
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        handler.removeCallbacksAndMessages(null)
    }
}

// 使用WeakReference
class LocationUpdateListener(fragment: Fragment) {
    private val fragmentRef = WeakReference(fragment)
    
    fun onLocationUpdate(location: Location) {
        fragmentRef.get()?.updateLocation(location)
    }
}
```

### 6.3 网络优化

#### 6.3.1 请求优化
```kotlin
// OkHttp配置
@Provides
@Singleton
fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .connectionPool(ConnectionPool(5, 5, TimeUnit.MINUTES))
        .cache(Cache(cacheDir, 10 * 1024 * 1024)) // 10MB缓存
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) BODY else NONE
        })
        .build()
}

// 请求合并
class BatchRequestManager {
    private val pendingRequests = mutableListOf<Request>()
    private val batchJob = SupervisorJob()
    
    fun addRequest(request: Request) {
        pendingRequests.add(request)
        scheduleBatch()
    }
    
    private fun scheduleBatch() {
        CoroutineScope(Dispatchers.IO + batchJob).launch {
            delay(100) // 等待100ms收集请求
            if (pendingRequests.isNotEmpty()) {
                executeBatch(pendingRequests.toList())
                pendingRequests.clear()
            }
        }
    }
}
```

### 6.4 数据库优化

#### 6.4.1 查询优化
```kotlin
@Dao
interface MessageDao {
    // 使用分页
    @Query("SELECT * FROM messages WHERE chat_id = :chatId ORDER BY created_at DESC")
    fun getMessagesPaged(chatId: String): PagingSource<Int, MessageEntity>
    
    // 批量操作
    @Transaction
    suspend fun updateMessages(messages: List<MessageEntity>) {
        messages.forEach { update(it) }
    }
    
    // 使用索引
    @Query("""
        SELECT * FROM messages 
        WHERE chat_id = :chatId 
        AND created_at > :timestamp
        ORDER BY created_at DESC
        LIMIT :limit
    """)
    suspend fun getRecentMessages(
        chatId: String, 
        timestamp: Long, 
        limit: Int
    ): List<MessageEntity>
}
```

---

## 7. 安全规范

### 7.1 数据安全

#### 7.1.1 敏感数据加密
```kotlin
// 使用EncryptedSharedPreferences
object SecurePreferences {
    private lateinit var encryptedPrefs: SharedPreferences
    
    fun init(context: Context) {
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
            
        encryptedPrefs = EncryptedSharedPreferences.create(
            context,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
    
    fun saveToken(token: String) {
        encryptedPrefs.edit().putString("auth_token", token).apply()
    }
    
    fun getToken(): String? {
        return encryptedPrefs.getString("auth_token", null)
    }
}
```

#### 7.1.2 网络安全
```kotlin
// 证书固定
fun getCertificatePinner(): CertificatePinner {
    return CertificatePinner.Builder()
        .add("api.petchat.com", "sha256/AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=")
        .build()
}

// 请求签名
class SignatureInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val timestamp = System.currentTimeMillis()
        val nonce = UUID.randomUUID().toString()
        
        val signature = generateSignature(
            request.method,
            request.url.encodedPath,
            timestamp,
            nonce
        )
        
        val newRequest = request.newBuilder()
            .header("X-Timestamp", timestamp.toString())
            .header("X-Nonce", nonce)
            .header("X-Signature", signature)
            .build()
            
        return chain.proceed(newRequest)
    }
}
```

### 7.2 代码安全

#### 7.2.1 ProGuard配置
```proguard
# 基础配置
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose

# 保留数据类
-keep class com.petchat.android.data.model.** { *; }
-keep class com.petchat.android.data.remote.dto.** { *; }

# 保留Room数据库
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keepclassmembers class * {
    @androidx.room.* <fields>;
}

# 混淆规则
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses
```

---

## 8. Git工作流

### 8.1 分支管理

#### 8.1.1 分支命名
```
main                    # 主分支，生产环境
develop                 # 开发分支
feature/chat-voice      # 功能分支
bugfix/crash-on-launch  # Bug修复分支
hotfix/security-patch   # 紧急修复分支
release/1.2.0          # 发布分支
```

#### 8.1.2 分支策略
```bash
# 创建功能分支
git checkout -b feature/chat-voice develop

# 完成功能后合并
git checkout develop
git merge --no-ff feature/chat-voice
git branch -d feature/chat-voice

# 创建发布分支
git checkout -b release/1.2.0 develop

# 发布后合并到main和develop
git checkout main
git merge --no-ff release/1.2.0
git tag -a v1.2.0 -m "Release version 1.2.0"

git checkout develop
git merge --no-ff release/1.2.0
```

### 8.2 提交规范

#### 8.2.1 提交消息格式
```
<type>(<scope>): <subject>

<body>

<footer>
```

#### 8.2.2 示例
```
feat(chat): 添加语音消息功能

- 集成语音录制SDK
- 实现语音消息UI
- 添加语音播放功能
- 支持语音转文字

Closes #123
```

#### 8.2.3 类型说明
| Type | 说明 |
|------|------|
| feat | 新功能 |
| fix | Bug修复 |
| docs | 文档更新 |
| style | 代码格式调整（不影响功能） |
| refactor | 重构（不增加功能，不修复bug） |
| perf | 性能优化 |
| test | 测试相关 |
| build | 构建系统或外部依赖变更 |
| ci | CI配置文件和脚本变更 |
| chore | 其他不修改src或test的变更 |
| revert | 回滚提交 |

### 8.3 Code Review

#### 8.3.1 Review清单
- [ ] 代码是否符合编码规范？
- [ ] 是否有适当的注释和文档？
- [ ] 是否处理了所有异常情况？
- [ ] 是否有性能问题？
- [ ] 是否有安全隐患？
- [ ] 测试覆盖是否充分？
- [ ] 是否影响现有功能？

#### 8.3.2 Review工具配置
```yaml
# .github/pull_request_template.md
## 变更说明
简要描述这个PR的改动

## 变更类型
- [ ] Bug修复
- [ ] 新功能
- [ ] 重构
- [ ] 文档更新

## 测试
- [ ] 单元测试通过
- [ ] UI测试通过
- [ ] 手动测试通过

## 截图（如果有UI变更）
请附上相关截图

## 相关Issue
Closes #xxx
```

---

## 9. 持续集成

### 9.1 GitHub Actions配置

#### 9.1.1 CI工作流
```yaml
# .github/workflows/android-ci.yml
name: Android CI

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 11
      uses: actions/setup-java@v3
      with:
        java-version: '11'
        distribution: 'temurin'
        
    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
      
    - name: Run tests
      run: ./gradlew test
      
    - name: Generate test report
      uses: dorny/test-reporter@v1
      if: success() || failure()
      with:
        name: Unit Tests
        path: app/build/test-results/test**/TEST-*.xml
        reporter: java-junit
        
  lint:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Run lint
      run: ./gradlew lint
      
    - name: Upload lint results
      uses: actions/upload-artifact@v3
      with:
        name: lint-results
        path: app/build/reports/lint-results-*.html
        
  build:
    runs-on: ubuntu-latest
    needs: [test, lint]
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Build debug APK
      run: ./gradlew assembleDebug
      
    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: app-debug
        path: app/build/outputs/apk/debug/app-debug.apk
```

#### 9.1.2 发布工作流
```yaml
# .github/workflows/release.yml
name: Release

on:
  push:
    tags:
      - 'v*'

jobs:
  release:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v3
    
    - name: Set up JDK 11
      uses: actions/setup-java@v3
      with:
        java-version: '11'
        distribution: 'temurin'
        
    - name: Decode keystore
      run: |
        echo "${{ secrets.KEYSTORE_BASE64 }}" | base64 -d > keystore.jks
        
    - name: Build release APK
      run: |
        ./gradlew assembleRelease \
          -Pandroid.injected.signing.store.file=keystore.jks \
          -Pandroid.injected.signing.store.password=${{ secrets.KEYSTORE_PASSWORD }} \
          -Pandroid.injected.signing.key.alias=${{ secrets.KEY_ALIAS }} \
          -Pandroid.injected.signing.key.password=${{ secrets.KEY_PASSWORD }}
          
    - name: Create Release
      uses: softprops/action-gh-release@v1
      with:
        files: app/build/outputs/apk/release/app-release.apk
        generate_release_notes: true
```

### 9.2 代码质量检查

#### 9.2.1 静态代码分析
```gradle
// app/build.gradle.kts
plugins {
    id("io.gitlab.arturbosch.detekt") version "1.22.0"
}

detekt {
    buildUponDefaultConfig = true
    allRules = false
    config = files("$projectDir/config/detekt/detekt.yml")
    baseline = file("$projectDir/config/detekt/baseline.xml")
}

tasks.withType<Detekt>().configureEach {
    reports {
        html.required.set(true)
        xml.required.set(true)
        txt.required.set(true)
        sarif.required.set(true)
    }
}
```

#### 9.2.2 依赖检查
```gradle
// 检查依赖更新
./gradlew dependencyUpdates

// 检查依赖漏洞
plugins {
    id("org.owasp.dependencycheck") version "7.4.4"
}

dependencyCheck {
    format = "HTML"
    suppressionFile = "config/owasp/suppressions.xml"
}
```

---

## 10. 故障排查

### 10.1 日志规范

#### 10.1.1 日志级别
```kotlin
object Logger {
    fun v(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, message)
        }
    }
    
    fun d(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message)
        }
    }
    
    fun i(tag: String, message: String) {
        Log.i(tag, message)
    }
    
    fun w(tag: String, message: String, throwable: Throwable? = null) {
        Log.w(tag, message, throwable)
    }
    
    fun e(tag: String, message: String, throwable: Throwable? = null) {
        Log.e(tag, message, throwable)
        // 生产环境发送到崩溃收集服务
        if (!BuildConfig.DEBUG) {
            FirebaseCrashlytics.getInstance().recordException(
                throwable ?: Exception(message)
            )
        }
    }
}
```

#### 10.1.2 日志使用
```kotlin
class ChatViewModel : ViewModel() {
    companion object {
        private const val TAG = "ChatViewModel"
    }
    
    fun sendMessage(content: String) {
        Logger.d(TAG, "Sending message: $content")
        
        viewModelScope.launch {
            try {
                val result = repository.sendMessage(content)
                Logger.i(TAG, "Message sent successfully")
            } catch (e: Exception) {
                Logger.e(TAG, "Failed to send message", e)
            }
        }
    }
}
```

### 10.2 崩溃处理

#### 10.2.1 全局异常处理
```kotlin
class PetChatApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // 设置全局异常处理
        Thread.setDefaultUncaughtExceptionHandler { thread, exception ->
            Logger.e("CRASH", "Uncaught exception in thread ${thread.name}", exception)
            
            // 记录崩溃信息
            CrashReporter.reportCrash(exception)
            
            // 重启应用
            restartApp()
        }
    }
    
    private fun restartApp() {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(
            this, 
            0, 
            intent, 
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.set(
            AlarmManager.RTC, 
            System.currentTimeMillis() + 100, 
            pendingIntent
        )
        
        // 退出应用
        exitProcess(0)
    }
}
```

#### 10.2.2 崩溃信息收集
```kotlin
object CrashReporter {
    fun reportCrash(throwable: Throwable) {
        val crashInfo = buildString {
            appendLine("=== CRASH REPORT ===")
            appendLine("Time: ${Date()}")
            appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
            appendLine("Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            appendLine("App Version: ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
            appendLine()
            appendLine("Stack Trace:")
            appendLine(throwable.stackTraceToString())
        }
        
        // 保存到文件
        saveCrashLog(crashInfo)
        
        // 发送到服务器
        if (!BuildConfig.DEBUG) {
            FirebaseCrashlytics.getInstance().recordException(throwable)
        }
    }
    
    private fun saveCrashLog(log: String) {
        try {
            val file = File(
                Environment.getExternalStorageDirectory(),
                "PetChat/crash_${System.currentTimeMillis()}.txt"
            )
            file.parentFile?.mkdirs()
            file.writeText(log)
        } catch (e: Exception) {
            Logger.e("CrashReporter", "Failed to save crash log", e)
        }
    }
}
```

### 10.3 性能监控

#### 10.3.1 方法追踪
```kotlin
inline fun <T> measureTimeMillis(tag: String, block: () -> T): T {
    val start = System.currentTimeMillis()
    val result = block()
    val duration = System.currentTimeMillis() - start
    Logger.d("Performance", "$tag took ${duration}ms")
    return result
}

// 使用
val messages = measureTimeMillis("LoadMessages") {
    repository.getMessages(chatId)
}
```

#### 10.3.2 内存监控
```kotlin
object MemoryMonitor {
    fun logMemoryUsage(tag: String) {
        val runtime = Runtime.getRuntime()
        val usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L
        val maxMemory = runtime.maxMemory() / 1048576L
        
        Logger.d("Memory", "$tag - Used: ${usedMemory}MB, Max: ${maxMemory}MB")
    }
    
    fun checkLowMemory(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memoryInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        
        return memoryInfo.lowMemory
    }
}
```

---

## 附录

### A. 常用工具类

#### A.1 扩展函数集合
```kotlin
// ViewExtensions.kt
fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.enable() {
    isEnabled = true
}

fun View.disable() {
    isEnabled = false
}

// ContextExtensions.kt
fun Context.dp2px(dp: Float): Int {
    return (dp * resources.displayMetrics.density).toInt()
}

fun Context.px2dp(px: Float): Int {
    return (px / resources.displayMetrics.density).toInt()
}

// StringExtensions.kt
fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPhone(): Boolean {
    return matches(Regex("^1[3-9]\\d{9}$"))
}
```

### B. 代码模板

#### B.1 Fragment模板
```kotlin
class ${NAME}Fragment : Fragment() {
    
    companion object {
        fun newInstance() = ${NAME}Fragment()
    }
    
    private var _binding: Fragment${NAME}Binding? = null
    private val binding get() = _binding!!
    
    private val viewModel: ${NAME}ViewModel by viewModels()
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = Fragment${NAME}Binding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        observeViewModel()
    }
    
    private fun setupUi() {
        // TODO: Setup UI
    }
    
    private fun observeViewModel() {
        // TODO: Observe ViewModel
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

### C. 检查清单

#### C.1 发布前检查
- [ ] 所有功能测试通过
- [ ] 无内存泄漏
- [ ] 性能指标达标
- [ ] 代码混淆正常
- [ ] 签名配置正确
- [ ] 版本号已更新
- [ ] 更新日志已编写
- [ ] 隐私政策已更新

---

*文档版本：1.0.0*  
*最后更新：2025-01-27*