# 🚀 Bütünleşik E-Ticaret Yönetim Sistemi

## 📋 Proje Açıklaması

Bu proje, **çoklu e-ticaret platformları** ile entegre çalışan, **gerçek zamanlı stok senkronizasyonu** sağlayan kapsamlı bir **stok takip ve yönetim sistemidir**. 

### 🎯 Ana Hedefler

- ✅ **Çoklu Platform Entegrasyonu**: eBay, Shopify, Amazon, Trendyol, Hepsiburada
- ✅ **Gerçek Zamanlı Stok Senkronizasyonu**: Tüm platformlarda anlık stok güncelleme
- ✅ **Merkezi Stok Yönetimi**: Tek noktadan tüm platformları yönetme
- ✅ **Otomatik Stok Güncelleme**: Satış/iade sonrası otomatik senkronizasyon
- ✅ **Webhook Desteği**: Gerçek zamanlı bildirimler ve güncellemeler
- ✅ **Kapsamlı Raporlama**: Detaylı analiz ve raporlama sistemi

## 🏗️ Sistem Mimarisi

### **Teknoloji Yığını**

#### **Backend:**
- **Spring Boot 3.4.5** - Ana framework
- **Java 17** - Programlama dili
- **PostgreSQL** - Ana veritabanı
- **Redis** - Cache ve session yönetimi
- **RabbitMQ** - Message queue
- **Spring Security** - Güvenlik
- **Spring Data JPA** - Veri erişim katmanı

#### **Frontend:**
- **Thymeleaf** - Template engine
- **Bootstrap 5** - CSS framework
- **JavaScript (ES6+)** - İnteraktif özellikler
- **Chart.js** - Grafik ve raporlama

#### **Entegrasyon:**
- **REST APIs** - Platform entegrasyonları
- **Webhooks** - Gerçek zamanlı bildirimler
- **OAuth 2.0** - Güvenli API erişimi
- **JSON** - Veri formatı

## 🚀 Kurulum ve Çalıştırma

### **Gereksinimler**

- Java 17+
- Maven 3.6+
- PostgreSQL 13+
- Redis 6+
- RabbitMQ 3.8+

### **1. Veritabanı Kurulumu**

```sql
-- PostgreSQL veritabanı oluştur
CREATE DATABASE stoktakip_ecommerce;
CREATE USER stoktakip_user WITH PASSWORD 'stoktakip_pass';
GRANT ALL PRIVILEGES ON DATABASE stoktakip_ecommerce TO stoktakip_user;
```

### **2. Redis Kurulumu**

```bash
# Ubuntu/Debian
sudo apt-get install redis-server

# macOS
brew install redis

# Windows
# Redis for Windows indirin ve kurun
```

### **3. RabbitMQ Kurulumu**

```bash
# Ubuntu/Debian
sudo apt-get install rabbitmq-server

# macOS
brew install rabbitmq

# Windows
# RabbitMQ for Windows indirin ve kurun
```

### **4. Uygulama Kurulumu**

```bash
# Projeyi klonla
git clone https://github.com/yigitcankayacann/Stok-Takip-Sistemi.git
cd Stok-Takip-Sistemi

# Bağımlılıkları yükle
mvn clean install

# Uygulamayı başlat
mvn spring-boot:run -Dspring-boot.run.profiles=ecommerce
```

### **5. Erişim**

- **Web Arayüzü**: http://localhost:8080/stoktakip
- **API Dokümantasyonu**: http://localhost:8080/stoktakip/swagger-ui.html
- **H2 Console**: http://localhost:8080/stoktakip/h2-console
- **Actuator**: http://localhost:8080/stoktakip/actuator

## 📊 Temel Özellikler

### **1. Platform Yönetimi**
- ✅ Platform ekleme/düzenleme/silme
- ✅ API kimlik bilgileri yönetimi
- ✅ Platform durumu takibi
- ✅ Bağlantı testi

### **2. Ürün Yönetimi**
- ✅ Ürün oluşturma/düzenleme/silme
- ✅ Platform'lara ürün gönderme
- ✅ Toplu ürün işlemleri
- ✅ Kategori yönetimi

### **3. Stok Yönetimi**
- ✅ Gerçek zamanlı stok takibi
- ✅ Otomatik stok senkronizasyonu
- ✅ Stok uyarıları
- ✅ Stok geçmişi

### **4. Sipariş Yönetimi**
- ✅ Platform siparişlerini çekme
- ✅ Sipariş durumu takibi
- ✅ Otomatik stok güncelleme
- ✅ İade/iptal işlemleri

### **5. Senkronizasyon**
- ✅ Otomatik senkronizasyon
- ✅ Manuel senkronizasyon
- ✅ Hata yönetimi ve retry
- ✅ Senkronizasyon logları

### **6. Raporlama**
- ✅ Stok raporları
- ✅ Satış raporları
- ✅ Platform performans raporları
- ✅ Senkronizasyon raporları

## 🔧 Konfigürasyon

### **Platform API Ayarları**

```properties
# eBay
ecommerce.ebay.api.endpoint=https://api.ebay.com
ecommerce.ebay.api.key=your-api-key
ecommerce.ebay.api.secret=your-api-secret

# Shopify
ecommerce.shopify.api.endpoint=https://your-shop.myshopify.com
ecommerce.shopify.api.token=your-access-token

# Amazon
ecommerce.amazon.api.endpoint=https://sellingpartnerapi-eu.amazon.com
ecommerce.amazon.api.key=your-api-key
ecommerce.amazon.api.secret=your-api-secret
```

### **Webhook Ayarları**

```properties
# Webhook URL'leri
webhook.ebay.url=https://your-domain.com/stoktakip/webhooks/ebay
webhook.shopify.url=https://your-domain.com/stoktakip/webhooks/shopify
webhook.amazon.url=https://your-domain.com/stoktakip/webhooks/amazon
```

## 📈 API Kullanımı

### **Stok Güncelleme**

```bash
POST /stoktakip/api/stock/update
Content-Type: application/json

{
  "productId": 1,
  "newStockQuantity": 50,
  "platforms": ["EBAY", "SHOPIFY"]
}
```

### **Platform Senkronizasyonu**

```bash
POST /stoktakip/api/sync/platform
Content-Type: application/json

{
  "platformId": 1,
  "syncType": "FULL_SYNC"
}
```

### **Webhook Test**

```bash
POST /stoktakip/webhooks/test/ebay
Content-Type: application/json

{
  "eventType": "INVENTORY_UPDATED",
  "productId": "123",
  "newStock": 25
}
```

## 🔒 Güvenlik

- **OAuth 2.0** ile güvenli API erişimi
- **JWT Token** tabanlı kimlik doğrulama
- **Webhook imza doğrulama**
- **HTTPS** zorunluluğu
- **Rate limiting** koruması

## 📊 Monitoring ve Logging

- **Spring Actuator** ile sistem durumu
- **Prometheus** metrikleri
- **Structured logging** (JSON format)
- **Error tracking** ve alerting
- **Performance monitoring**

## 🚀 Deployment

### **Docker ile Deployment**

```dockerfile
FROM openjdk:17-jdk-slim
COPY target/stoktakip-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### **Docker Compose**

```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=ecommerce
    depends_on:
      - postgres
      - redis
      - rabbitmq
  
  postgres:
    image: postgres:13
    environment:
      POSTGRES_DB: stoktakip_ecommerce
      POSTGRES_USER: stoktakip_user
      POSTGRES_PASSWORD: stoktakip_pass
  
  redis:
    image: redis:6-alpine
  
  rabbitmq:
    image: rabbitmq:3-management
```

## 🧪 Test

```bash
# Unit testler
mvn test

# Integration testler
mvn verify

# Test coverage
mvn jacoco:report
```

## 📝 Lisans

Bu proje MIT lisansı altında lisanslanmıştır.

## 🤝 Katkıda Bulunma

1. Fork yapın
2. Feature branch oluşturun (`git checkout -b feature/amazing-feature`)
3. Commit yapın (`git commit -m 'Add amazing feature'`)
4. Push yapın (`git push origin feature/amazing-feature`)
5. Pull Request oluşturun

## 📞 İletişim

- **Proje Sahibi**: yigitcankayacann
- **Email**: your-email@example.com
- **GitHub**: https://github.com/yigitcankayacann/Stok-Takip-Sistemi

## 🙏 Teşekkürler

- Spring Boot ekibi
- Bootstrap ekibi
- Tüm açık kaynak katkıda bulunanlar
