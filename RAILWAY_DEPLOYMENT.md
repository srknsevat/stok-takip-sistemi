# Railway Deployment Rehberi

## 🚀 Railway'de Deploy Etme Adımları

### 1. Railway Hesabı ve Proje Oluşturma
1. [Railway.app](https://railway.app) adresine gidin
2. GitHub hesabınızla giriş yapın
3. "New Project" butonuna tıklayın
4. "Deploy from GitHub repo" seçin
5. Bu repository'yi seçin

### 2. Environment Variables Ayarlama
Railway dashboard'da "Variables" sekmesine gidin ve aşağıdaki değişkenleri ekleyin:

#### Veritabanı Ayarları
```
DATABASE_URL=postgresql://username:password@host:port/database
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
```

#### Redis Ayarları (Opsiyonel)
```
REDIS_URL=your_redis_host
REDIS_PORT=6379
REDIS_PASSWORD=your_redis_password
```

#### RabbitMQ Ayarları (Opsiyonel)
```
RABBITMQ_URL=your_rabbitmq_host
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=your_username
RABBITMQ_PASSWORD=your_password
```

#### Şifreleme Anahtarı
```
ENCRYPTION_KEY=your_32_character_encryption_key_here
```

#### E-ticaret Platform Ayarları
```
EBAY_API_ENDPOINT=https://api.ebay.com
SHOPIFY_API_ENDPOINT=https://your-shop.myshopify.com
AMAZON_API_ENDPOINT=https://sellingpartnerapi-na.amazon.com
TRENDYOL_API_ENDPOINT=https://api.trendyol.com
```

#### Platform API Kimlik Bilgileri
```
EBAY_CLIENT_ID=your_ebay_client_id
EBAY_CLIENT_SECRET=your_ebay_client_secret
EBAY_RUNAME=your_ebay_runame

SHOPIFY_API_KEY=your_shopify_api_key
SHOPIFY_API_SECRET=your_shopify_api_secret
SHOPIFY_ACCESS_TOKEN=your_shopify_access_token
```

#### Webhook Secret Keys
```
EBAY_WEBHOOK_SECRET=your_ebay_webhook_secret
SHOPIFY_WEBHOOK_SECRET=your_shopify_webhook_secret
```

#### Mail Ayarları (Opsiyonel)
```
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_app_password
```

#### Admin Kullanıcı Ayarları
```
ADMIN_USERNAME=admin
ADMIN_PASSWORD=your_secure_password
```

### 3. PostgreSQL Veritabanı Ekleme
1. Railway dashboard'da "Add Service" butonuna tıklayın
2. "Database" → "PostgreSQL" seçin
3. Veritabanı otomatik olarak oluşturulacak
4. `DATABASE_URL` environment variable'ı otomatik olarak ayarlanacak

### 4. Redis Ekleme (Opsiyonel)
1. "Add Service" → "Database" → "Redis" seçin
2. Redis servisi otomatik olarak oluşturulacak

### 5. RabbitMQ Ekleme (Opsiyonel)
1. "Add Service" → "Database" → "RabbitMQ" seçin
2. RabbitMQ servisi otomatik olarak oluşturulacak

### 6. Deploy Etme
1. Railway otomatik olarak GitHub'dan kodu çekecek
2. Dockerfile kullanarak uygulamayı build edecek
3. Uygulama `railway` profile'ı ile çalışacak
4. Port 8080'de yayınlanacak

### 7. Domain Ayarlama
1. Railway dashboard'da "Settings" → "Domains" sekmesine gidin
2. "Generate Domain" butonuna tıklayın
3. Veya kendi domain'inizi ekleyin

## 🔧 Troubleshooting

### Yaygın Hatalar ve Çözümleri

#### 1. Database Connection Hatası
```
Error: Could not connect to database
```
**Çözüm:**
- `DATABASE_URL` environment variable'ının doğru olduğundan emin olun
- PostgreSQL servisinin çalıştığından emin olun

#### 2. Port Binding Hatası
```
Error: Port already in use
```
**Çözüm:**
- Railway otomatik olarak `PORT` environment variable'ını ayarlar
- `server.port=${PORT:8080}` konfigürasyonu doğru çalışır

#### 3. Memory Hatası
```
Error: OutOfMemoryError
```
**Çözüm:**
- Railway'de daha yüksek plan seçin
- JVM heap size'ı ayarlayın: `-Xmx512m`

#### 4. Build Hatası
```
Error: Maven build failed
```
**Çözüm:**
- `pom.xml` dosyasındaki dependency'leri kontrol edin
- Java 17 kullandığınızdan emin olun

#### 5. Profile Hatası
```
Error: Could not find profile 'railway'
```
**Çözüm:**
- `application-railway.properties` dosyasının mevcut olduğundan emin olun
- Dockerfile'da doğru profile'ın kullanıldığından emin olun

## 📊 Monitoring

### Health Check
- Uygulama: `https://your-app.railway.app/actuator/health`
- Metrics: `https://your-app.railway.app/actuator/metrics`

### Logs
- Railway dashboard'da "Deployments" sekmesinden logları görüntüleyin
- Real-time log takibi yapabilirsiniz

## 🔒 Güvenlik

### Environment Variables
- Hassas bilgileri environment variables olarak saklayın
- `ENCRYPTION_KEY`'i güçlü bir değer olarak ayarlayın
- API key'leri düzenli olarak değiştirin

### Database
- PostgreSQL bağlantısı SSL ile şifrelenir
- Veritabanı şifreleri güçlü olmalıdır

## 🚀 Production Optimizasyonları

### 1. JVM Ayarları
```bash
JAVA_OPTS=-Xmx1g -Xms512m -XX:+UseG1GC
```

### 2. Database Connection Pool
```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
```

### 3. Caching
```properties
spring.cache.type=redis
spring.cache.redis.time-to-live=600000
```

### 4. Logging
```properties
logging.level.com.ornek.stoktakip=INFO
logging.level.org.springframework=WARN
```

## 📱 Mobil Uygulama Entegrasyonu

### API Endpoints
- Base URL: `https://your-app.railway.app`
- API Documentation: `https://your-app.railway.app/swagger-ui.html`

### Authentication
- JWT token kullanın
- API key'leri güvenli şekilde saklayın

## 🔄 CI/CD Pipeline

### GitHub Actions (Opsiyonel)
```yaml
name: Deploy to Railway
on:
  push:
    branches: [main]
jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Deploy to Railway
        uses: railway-app/railway-deploy@v1
        with:
          railway-token: ${{ secrets.RAILWAY_TOKEN }}
```

## 📞 Destek

### Railway Destek
- [Railway Documentation](https://docs.railway.app)
- [Railway Discord](https://discord.gg/railway)

### Uygulama Destek
- GitHub Issues: Bu repository'de issue açın
- Email: support@yourcompany.com

---

**🎉 Tebrikler! Uygulamanız Railway'de başarıyla çalışıyor!**
