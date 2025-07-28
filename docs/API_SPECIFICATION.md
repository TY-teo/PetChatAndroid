# PetChat Android API 接口规范文档

## 目录

1. [概述](#1-概述)
2. [通用规范](#2-通用规范)
3. [认证接口](#3-认证接口)
4. [宠物管理接口](#4-宠物管理接口)
5. [聊天接口](#5-聊天接口)
6. [定位接口](#6-定位接口)
7. [社交接口](#7-社交接口)
8. [WebSocket接口](#8-websocket接口)

---

## 1. 概述

### 1.1 基础信息
- **基础URL**: `https://api.petchat.com/v1`
- **协议**: HTTPS
- **数据格式**: JSON
- **字符编码**: UTF-8
- **API版本**: v1

### 1.2 环境说明
| 环境 | 基础URL | 用途 |
|------|---------|------|
| 开发 | `https://dev-api.petchat.com/v1` | 开发测试 |
| 测试 | `https://staging-api.petchat.com/v1` | 集成测试 |
| 生产 | `https://api.petchat.com/v1` | 正式环境 |

---

## 2. 通用规范

### 2.1 请求格式

#### 2.1.1 请求头
```http
Content-Type: application/json
Accept: application/json
Authorization: Bearer {access_token}
X-App-Version: 1.0.0
X-Device-Id: {device_uuid}
X-Platform: Android
```

#### 2.1.2 请求示例
```http
POST /api/v1/pets
Host: api.petchat.com
Content-Type: application/json
Authorization: Bearer eyJhbGciOiJIUzI1NiIs...

{
    "name": "小白",
    "type": "DOG",
    "breed": "金毛",
    "gender": "MALE",
    "birthDate": "2022-03-15"
}
```

### 2.2 响应格式

#### 2.2.1 成功响应
```json
{
    "code": 0,
    "message": "success",
    "data": {
        // 响应数据
    },
    "timestamp": 1643723400000
}
```

#### 2.2.2 错误响应
```json
{
    "code": 40001,
    "message": "参数错误",
    "errors": [
        {
            "field": "name",
            "message": "宠物名称不能为空"
        }
    ],
    "timestamp": 1643723400000
}
```

### 2.3 错误码规范

| 错误码 | 说明 | HTTP状态码 |
|--------|------|------------|
| 0 | 成功 | 200 |
| 40001 | 参数错误 | 400 |
| 40101 | 未认证 | 401 |
| 40301 | 无权限 | 403 |
| 40401 | 资源不存在 | 404 |
| 40901 | 请求冲突 | 409 |
| 42901 | 请求过于频繁 | 429 |
| 50001 | 服务器内部错误 | 500 |
| 50301 | 服务不可用 | 503 |

### 2.4 分页规范

#### 2.4.1 请求参数
```
GET /api/v1/moments?page=1&size=20&sort=createTime,desc
```

| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码，从1开始，默认1 |
| size | int | 否 | 每页数量，默认20，最大100 |
| sort | string | 否 | 排序规则，格式：字段名,方向 |

#### 2.4.2 响应格式
```json
{
    "code": 0,
    "message": "success",
    "data": {
        "content": [...],
        "totalElements": 100,
        "totalPages": 5,
        "size": 20,
        "number": 0,
        "first": true,
        "last": false
    }
}
```

---

## 3. 认证接口

### 3.1 用户注册

**接口**: `POST /auth/register`

**请求体**:
```json
{
    "phone": "13800138000",
    "password": "Abc123456",
    "nickname": "宠物爱好者",
    "verificationCode": "123456"
}
```

**响应**:
```json
{
    "code": 0,
    "message": "success",
    "data": {
        "userId": "user_123456",
        "phone": "13800138000",
        "nickname": "宠物爱好者",
        "avatar": null,
        "createTime": 1643723400000
    }
}
```

### 3.2 用户登录

**接口**: `POST /auth/login`

**请求体**:
```json
{
    "phone": "13800138000",
    "password": "Abc123456",
    "deviceId": "device_uuid_123"
}
```

**响应**:
```json
{
    "code": 0,
    "message": "success",
    "data": {
        "accessToken": "eyJhbGciOiJIUzI1NiIs...",
        "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
        "expiresIn": 7200,
        "user": {
            "userId": "user_123456",
            "phone": "13800138000",
            "nickname": "宠物爱好者",
            "avatar": "https://cdn.petchat.com/avatars/user_123456.jpg"
        }
    }
}
```

### 3.3 刷新Token

**接口**: `POST /auth/refresh`

**请求体**:
```json
{
    "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

**响应**:
```json
{
    "code": 0,
    "message": "success",
    "data": {
        "accessToken": "eyJhbGciOiJIUzI1NiIs...",
        "expiresIn": 7200
    }
}
```

---

## 4. 宠物管理接口

### 4.1 添加宠物

**接口**: `POST /pets`

**请求体**:
```json
{
    "name": "小白",
    "type": "DOG",
    "breed": "金毛寻回犬",
    "gender": "MALE",
    "birthDate": "2022-03-15",
    "weight": 25.5,
    "description": "活泼可爱的金毛"
}
```

**响应**:
```json
{
    "code": 0,
    "message": "success",
    "data": {
        "petId": "pet_123456",
        "name": "小白",
        "type": "DOG",
        "breed": "金毛寻回犬",
        "gender": "MALE",
        "birthDate": "2022-03-15",
        "age": "1岁3个月",
        "weight": 25.5,
        "avatar": null,
        "description": "活泼可爱的金毛",
        "createTime": 1643723400000
    }
}
```

### 4.2 获取宠物列表

**接口**: `GET /pets`

**请求参数**: 无

**响应**:
```json
{
    "code": 0,
    "message": "success",
    "data": [
        {
            "petId": "pet_123456",
            "name": "小白",
            "type": "DOG",
            "breed": "金毛寻回犬",
            "gender": "MALE",
            "age": "1岁3个月",
            "avatar": "https://cdn.petchat.com/pets/pet_123456.jpg",
            "isOnline": true,
            "lastLocation": {
                "latitude": 31.2304,
                "longitude": 121.4737,
                "address": "上海市黄浦区人民广场",
                "updateTime": 1643723400000
            }
        }
    ]
}
```

### 4.3 更新宠物信息

**接口**: `PUT /pets/{petId}`

**请求体**:
```json
{
    "name": "小白",
    "weight": 26.0,
    "description": "活泼可爱的大金毛"
}
```

**响应**:
```json
{
    "code": 0,
    "message": "success",
    "data": {
        "petId": "pet_123456",
        "name": "小白",
        "weight": 26.0,
        "description": "活泼可爱的大金毛",
        "updateTime": 1643723400000
    }
}
```

### 4.4 上传宠物头像

**接口**: `POST /pets/{petId}/avatar`

**请求格式**: `multipart/form-data`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| file | File | 是 | 图片文件，支持jpg/png，最大5MB |

**响应**:
```json
{
    "code": 0,
    "message": "success",
    "data": {
        "avatarUrl": "https://cdn.petchat.com/pets/pet_123456.jpg"
    }
}
```

---

## 5. 聊天接口

### 5.1 发送消息

**接口**: `POST /chat/messages`

**请求体**:
```json
{
    "petId": "pet_123456",
    "content": "小白，你饿了吗？",
    "type": "TEXT"
}
```

**响应**:
```json
{
    "code": 0,
    "message": "success",
    "data": {
        "messageId": "msg_789012",
        "chatId": "chat_123456",
        "content": "小白，你饿了吗？",
        "type": "TEXT",
        "sender": "USER",
        "createTime": 1643723400000,
        "petResponse": {
            "messageId": "msg_789013",
            "content": "汪汪！我想吃好吃的！",
            "emotion": "HUNGRY",
            "createTime": 1643723401000
        }
    }
}
```

### 5.2 获取聊天记录

**接口**: `GET /chat/{petId}/messages`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| page | int | 否 | 页码 |
| size | int | 否 | 每页数量 |
| before | long | 否 | 获取此时间之前的消息 |

**响应**:
```json
{
    "code": 0,
    "message": "success",
    "data": {
        "content": [
            {
                "messageId": "msg_789012",
                "content": "小白，你饿了吗？",
                "type": "TEXT",
                "sender": "USER",
                "createTime": 1643723400000
            },
            {
                "messageId": "msg_789013",
                "content": "汪汪！我想吃好吃的！",
                "type": "TEXT",
                "sender": "PET",
                "emotion": "HUNGRY",
                "createTime": 1643723401000
            }
        ],
        "hasMore": true
    }
}
```

### 5.3 发送语音消息

**接口**: `POST /chat/voice`

**请求格式**: `multipart/form-data`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| petId | string | 是 | 宠物ID |
| voice | File | 是 | 语音文件，支持mp3/m4a，最大1MB |
| duration | int | 是 | 语音时长（秒） |

**响应**:
```json
{
    "code": 0,
    "message": "success",
    "data": {
        "messageId": "msg_789014",
        "type": "VOICE",
        "voiceUrl": "https://cdn.petchat.com/voices/msg_789014.m4a",
        "duration": 3,
        "transcription": "小白，过来",
        "petResponse": {
            "messageId": "msg_789015",
            "content": "汪汪！我来了！",
            "emotion": "EXCITED"
        }
    }
}
```

---

## 6. 定位接口

### 6.1 更新宠物位置

**接口**: `POST /location/update`

**请求体**:
```json
{
    "petId": "pet_123456",
    "latitude": 31.2304,
    "longitude": 121.4737,
    "accuracy": 10.5,
    "battery": 85
}
```

**响应**:
```json
{
    "code": 0,
    "message": "success",
    "data": {
        "locationId": "loc_345678",
        "address": "上海市黄浦区人民广场",
        "isInSafeZone": true
    }
}
```

### 6.2 获取实时位置

**接口**: `GET /location/{petId}/current`

**响应**:
```json
{
    "code": 0,
    "message": "success",
    "data": {
        "latitude": 31.2304,
        "longitude": 121.4737,
        "accuracy": 10.5,
        "address": "上海市黄浦区人民广场",
        "battery": 85,
        "updateTime": 1643723400000,
        "isOnline": true,
        "speed": 0,
        "direction": 0
    }
}
```

### 6.3 获取历史轨迹

**接口**: `GET /location/{petId}/history`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| startTime | long | 是 | 开始时间戳 |
| endTime | long | 是 | 结束时间戳 |

**响应**:
```json
{
    "code": 0,
    "message": "success",
    "data": {
        "tracks": [
            {
                "latitude": 31.2304,
                "longitude": 121.4737,
                "timestamp": 1643723400000
            },
            {
                "latitude": 31.2314,
                "longitude": 121.4747,
                "timestamp": 1643723460000
            }
        ],
        "distance": 156.5,
        "duration": 600,
        "avgSpeed": 0.94
    }
}
```

### 6.4 设置电子围栏

**接口**: `POST /location/geofence`

**请求体**:
```json
{
    "petId": "pet_123456",
    "name": "家附近",
    "centerLat": 31.2304,
    "centerLng": 121.4737,
    "radius": 500,
    "enabled": true
}
```

**响应**:
```json
{
    "code": 0,
    "message": "success",
    "data": {
        "geofenceId": "geo_123456",
        "name": "家附近",
        "createTime": 1643723400000
    }
}
```

---

## 7. 社交接口

### 7.1 发布动态

**接口**: `POST /moments`

**请求格式**: `multipart/form-data`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| petId | string | 是 | 宠物ID |
| content | string | 是 | 动态内容 |
| images | File[] | 否 | 图片文件，最多9张 |
| latitude | double | 否 | 纬度 |
| longitude | double | 否 | 经度 |

**响应**:
```json
{
    "code": 0,
    "message": "success",
    "data": {
        "momentId": "moment_123456",
        "createTime": 1643723400000
    }
}
```

### 7.2 获取动态列表

**接口**: `GET /moments`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| type | string | 否 | 类型：following/nearby/hot |
| latitude | double | 否 | 附近动态需要 |
| longitude | double | 否 | 附近动态需要 |
| page | int | 否 | 页码 |
| size | int | 否 | 每页数量 |

**响应**:
```json
{
    "code": 0,
    "message": "success",
    "data": {
        "content": [
            {
                "momentId": "moment_123456",
                "pet": {
                    "petId": "pet_123456",
                    "name": "小白",
                    "avatar": "https://cdn.petchat.com/pets/pet_123456.jpg"
                },
                "content": "今天带小白去公园玩，开心！",
                "images": [
                    "https://cdn.petchat.com/moments/img1.jpg",
                    "https://cdn.petchat.com/moments/img2.jpg"
                ],
                "location": {
                    "latitude": 31.2304,
                    "longitude": 121.4737,
                    "address": "人民公园"
                },
                "likes": 23,
                "comments": 5,
                "isLiked": false,
                "createTime": 1643723400000
            }
        ],
        "hasMore": true
    }
}
```

### 7.3 点赞/取消点赞

**接口**: `POST /moments/{momentId}/like`

**请求体**:
```json
{
    "action": "like" // like或unlike
}
```

**响应**:
```json
{
    "code": 0,
    "message": "success",
    "data": {
        "likes": 24,
        "isLiked": true
    }
}
```

### 7.4 评论动态

**接口**: `POST /moments/{momentId}/comments`

**请求体**:
```json
{
    "content": "好可爱的狗狗！",
    "replyTo": null
}
```

**响应**:
```json
{
    "code": 0,
    "message": "success",
    "data": {
        "commentId": "comment_123456",
        "content": "好可爱的狗狗！",
        "user": {
            "userId": "user_123456",
            "nickname": "宠物爱好者",
            "avatar": "https://cdn.petchat.com/avatars/user_123456.jpg"
        },
        "createTime": 1643723400000
    }
}
```

---

## 8. WebSocket接口

### 8.1 连接建立

**WebSocket URL**: `wss://ws.petchat.com/v1/connect`

**连接参数**:
```
wss://ws.petchat.com/v1/connect?token={access_token}&deviceId={device_id}
```

### 8.2 消息格式

#### 8.2.1 客户端发送
```json
{
    "type": "message",
    "data": {
        "petId": "pet_123456",
        "content": "你好小白"
    },
    "messageId": "client_msg_123",
    "timestamp": 1643723400000
}
```

#### 8.2.2 服务端推送
```json
{
    "type": "pet_response",
    "data": {
        "petId": "pet_123456",
        "content": "汪汪！主人好~",
        "emotion": "HAPPY"
    },
    "messageId": "server_msg_456",
    "timestamp": 1643723401000
}
```

### 8.3 事件类型

| 事件类型 | 说明 | 方向 |
|---------|------|------|
| connect | 连接成功 | S→C |
| message | 发送消息 | C→S |
| pet_response | 宠物回复 | S→C |
| location_update | 位置更新 | S→C |
| geofence_alert | 围栏告警 | S→C |
| pet_status | 宠物状态变化 | S→C |
| heartbeat | 心跳保活 | C↔S |
| error | 错误信息 | S→C |

### 8.4 心跳机制

客户端每30秒发送一次心跳：
```json
{
    "type": "heartbeat",
    "timestamp": 1643723400000
}
```

服务端响应：
```json
{
    "type": "heartbeat_ack",
    "timestamp": 1643723400000
}
```

### 8.5 断线重连

1. 检测到连接断开后，客户端应该实现指数退避重连
2. 重连间隔：1s → 2s → 4s → 8s → 16s → 30s（最大）
3. 重连时携带上次收到的最后一条消息ID，用于消息补发

---

## 附录

### A. 数据字典

#### 宠物类型（PetType）
| 值 | 说明 |
|----|------|
| DOG | 狗 |
| CAT | 猫 |
| BIRD | 鸟 |
| RABBIT | 兔子 |
| OTHER | 其他 |

#### 性别（Gender）
| 值 | 说明 |
|----|------|
| MALE | 雄性 |
| FEMALE | 雌性 |

#### 宠物情绪（PetEmotion）
| 值 | 说明 |
|----|------|
| NORMAL | 正常 |
| HAPPY | 开心 |
| SAD | 难过 |
| HUNGRY | 饥饿 |
| TIRED | 疲劳 |
| EXCITED | 兴奋 |
| PLAYFUL | 想玩耍 |
| CURIOUS | 好奇 |

### B. 限流规则

| 接口类型 | 限制 |
|---------|------|
| 认证接口 | 5次/分钟 |
| 查询接口 | 60次/分钟 |
| 写入接口 | 30次/分钟 |
| 上传接口 | 10次/分钟 |

### C. 文件上传限制

| 文件类型 | 格式 | 大小限制 |
|---------|------|----------|
| 头像 | JPG/PNG | 5MB |
| 动态图片 | JPG/PNG | 10MB |
| 语音消息 | MP3/M4A | 1MB |
| 视频 | MP4 | 100MB |

---

*文档版本：1.0.0*  
*最后更新：2025-01-27*