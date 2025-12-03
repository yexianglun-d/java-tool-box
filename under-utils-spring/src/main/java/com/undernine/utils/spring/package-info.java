/**
 * Spring 通用组件模块
 * <p>
 * 提供 Spring 项目常用的通用组件，包括统一返回结果、异常处理、AOP工具、HTTP拦截器等企业级功能。
 * </p>
 *
 * <h2>📦 包结构</h2>
 * <ul>
 *     <li><b>annotation/</b> - AOP注解定义（OperationLog、PreventRepeat、RateLimit、Retry）</li>
 *     <li><b>aspect/</b> - AOP切面实现（日志、防重复、限流、重试）</li>
 *     <li><b>interceptor/</b> - HTTP拦截器（请求日志）</li>
 *     <li><b>enums/</b> - 枚举类（操作类型）</li>
 *     <li><b>context/</b> - Spring上下文工具</li>
 *     <li><b>exception/</b> - 异常处理</li>
 *     <li><b>result/</b> - 统一响应</li>
 * </ul>
 *
 * <h2>🚀 快速开始</h2>
 * <h3>启用AOP</h3>
 * <pre>{@code
 * @SpringBootApplication
 * @EnableAspectJAutoProxy
 * @ComponentScan(basePackages = {
 *     "com.yourcompany",
 *     "com.undernine.utils.spring"
 * })
 * public class Application {
 *     public static void main(String[] args) {
 *         SpringApplication.run(Application.class, args);
 *     }
 * }
 * }</pre>
 *
 * <h2>📋 核心功能</h2>
 *
 * <h3>1. 统一返回结果</h3>
 * <p>使用 {@link com.undernine.utils.spring.result.Result} 统一API响应格式</p>
 * <pre>{@code
 * @RestController
 * public class UserController {
 *     @GetMapping("/user/{id}")
 *     public Result<User> getUser(@PathVariable Long id) {
 *         User user = userService.getById(id);
 *         return Result.success(user);
 *     }
 * }
 * }</pre>
 *
 * <h3>2. 业务异常处理</h3>
 * <p>使用 {@link com.undernine.utils.spring.exception.BizException} 抛出业务异常</p>
 * <p>{@link com.undernine.utils.spring.exception.GlobalExceptionHandler} 自动捕获并转换为统一响应</p>
 * <pre>{@code
 * @Service
 * public class UserService {
 *     public User getById(Long id) {
 *         User user = userMapper.selectById(id);
 *         if (user == null) {
 *             throw new BizException("用户不存在");
 *         }
 *         return user;
 *     }
 * }
 * }</pre>
 *
 * <h3>3. Spring 上下文工具</h3>
 * <p>使用 {@link com.undernine.utils.spring.context.SpringContextHolder} 在非Spring管理的类中获取Bean</p>
 * <pre>{@code
 * UserService userService = SpringContextHolder.getBean(UserService.class);
 * String property = SpringContextHolder.getProperty("app.name", "default");
 * }</pre>
 *
 * <h3>4. 操作日志 @OperationLog</h3>
 * <p>使用 {@link com.undernine.utils.spring.annotation.OperationLog} 自动记录用户操作行为</p>
 * <pre>{@code
 * @PostMapping("/users")
 * @OperationLog(module = "用户管理", type = OperationType.CREATE, 
 *              content = "创建用户", recordParams = true, recordResult = true)
 * public Result<User> createUser(@RequestBody User user) {
 *     return Result.success(userService.create(user));
 * }
 * }</pre>
 * <p><b>日志输出：</b>【操作日志】模块: 用户管理 | 操作: 新增 | 内容: 创建用户 | 用户: admin | IP: 192.168.1.100</p>
 *
 * <h3>5. 防重复提交 @PreventRepeat</h3>
 * <p>使用 {@link com.undernine.utils.spring.annotation.PreventRepeat} 防止短时间内重复提交</p>
 * <pre>{@code
 * @PostMapping("/orders")
 * @PreventRepeat(timeout = 5, timeUnit = TimeUnit.SECONDS, message = "请勿重复提交订单")
 * public Result<Order> createOrder(@RequestBody Order order) {
 *     return Result.success(orderService.create(order));
 * }
 * }</pre>
 * <p><b>实现方式：</b>基于内存缓存，使用 用户ID + 方法签名 + 参数 生成唯一键</p>
 *
 * <h3>6. 接口限流 @RateLimit</h3>
 * <p>使用 {@link com.undernine.utils.spring.annotation.RateLimit} 限制接口访问频率</p>
 * <pre>{@code
 * @GetMapping("/sms/send")
 * @RateLimit(limit = 10, period = 60, message = "发送短信过于频繁，请稍后再试")
 * public Result<Void> sendSms(@RequestParam String phone) {
 *     smsService.send(phone);
 *     return Result.success();
 * }
 * }</pre>
 * <p><b>实现方式：</b>令牌桶算法，每个用户独立计数</p>
 *
 * <h3>7. 重试机制 @Retry</h3>
 * <p>使用 {@link com.undernine.utils.spring.annotation.Retry} 自动重试失败的方法</p>
 * <pre>{@code
 * @Retry(maxAttempts = 3, delay = 1000, exceptions = {IOException.class, TimeoutException.class})
 * public String callExternalApi() {
 *     return httpClient.get("https://api.example.com/data");
 * }
 * }</pre>
 * <p><b>重试策略：</b>最多重试3次，每次间隔1秒，仅针对指定异常类型重试</p>
 *
 * <h3>8. HTTP请求日志 RequestLogInterceptor</h3>
 * <p>使用 {@link com.undernine.utils.spring.interceptor.RequestLogInterceptor} 记录所有HTTP请求</p>
 * <pre>{@code
 * @Configuration
 * public class WebMvcConfig implements WebMvcConfigurer {
 *     @Override
 *     public void addInterceptors(InterceptorRegistry registry) {
 *         registry.addInterceptor(new RequestLogInterceptor())
 *                 .addPathPatterns("/**")
 *                 .excludePathPatterns("/static/**");
 *     }
 * }
 * }</pre>
 * <p><b>日志输出：</b>【HTTP请求】POST /api/users 来自IP: 192.168.1.100</p>
 * <p><b>日志输出：</b>【HTTP响应】POST /api/users - 状态码: 200, 耗时: 156ms</p>
 *
 * <h3>9. 敏感信息脱敏 @Sensitive</h3>
 * <p>使用 {@link com.undernine.utils.spring.annotation.Sensitive} 自动对敏感字段脱敏</p>
 * <pre>{@code
 * public class User {
 *     @Sensitive(type = SensitiveType.MOBILE_PHONE)
 *     private String phone;  // JSON输出：138****5678
 *     
 *     @Sensitive(type = SensitiveType.ID_CARD)
 *     private String idCard; // JSON输出：320***********1234
 *     
 *     @Sensitive(type = SensitiveType.EMAIL)
 *     private String email;  // JSON输出：a***@example.com
 *     
 *     @Sensitive(type = SensitiveType.BANK_CARD)
 *     private String bankCard; // JSON输出：6222 **** **** 1234
 * }
 * }</pre>
 * <p><b>支持类型：</b>手机号、身份证、银行卡、邮箱、姓名、地址、密码、固定电话、车牌号</p>
 * <p><b>自定义规则：</b>{@code @Sensitive(type = CUSTOM, customRule = "3,4")} 保留前3位后4位</p>
 * <p>工具类调用：{@code DesensitizeUtils.mobilePhone("13812345678")} → 138****5678</p>
 *
 * <h2>🔧 扩展指南</h2>
 *
 * <h3>扩展为分布式版本（Redis）</h3>
 * <p>将内存版防重复提交和限流扩展为基于Redis的分布式实现：</p>
 * <pre>{@code
 * @Aspect
 * @Component
 * public class RedisPreventRepeatAspect {
 *     @Autowired
 *     private RedisTemplate<String, String> redisTemplate;
 *     
 *     @Around("@annotation(preventRepeat)")
 *     public Object around(ProceedingJoinPoint point, PreventRepeat preventRepeat) throws Throwable {
 *         String key = "prevent:" + getUserId() + ":" + getMethodKey(point);
 *         Boolean success = redisTemplate.opsForValue()
 *             .setIfAbsent(key, "1", preventRepeat.timeout(), preventRepeat.timeUnit());
 *         if (Boolean.FALSE.equals(success)) {
 *             throw new BizException(preventRepeat.message());
 *         }
 *         return point.proceed();
 *     }
 * }
 * }</pre>
 *
 * <h3>集成实际认证系统</h3>
 * <p>获取当前登录用户信息：</p>
 * <pre>{@code
 * // Spring Security
 * private String getCurrentUsername() {
 *     Authentication auth = SecurityContextHolder.getContext().getAuthentication();
 *     return auth != null ? auth.getName() : "anonymous";
 * }
 * 
 * // JWT
 * private String getCurrentUsername() {
 *     String token = request.getHeader("Authorization");
 *     return jwtUtil.getUsernameFromToken(token);
 * }
 * }</pre>
 *
 * <h3>操作日志持久化</h3>
 * <p>将操作日志保存到数据库：</p>
 * <pre>{@code
 * @Async
 * public void saveOperationLog(OperationLogEntity log) {
 *     log.setCreateTime(LocalDateTime.now());
 *     operationLogMapper.insert(log);
 * }
 * }</pre>
 *
 * <h3>慢请求告警</h3>
 * <p>在 RequestLogInterceptor 中添加慢请求监控：</p>
 * <pre>{@code
 * @Override
 * public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
 *                             Object handler, Exception ex) {
 *     long elapsed = System.currentTimeMillis() - startTime;
 *     if (elapsed > 3000) {
 *         log.warn("【慢请求告警】{} {} - 耗时: {}ms", request.getMethod(), 
 *                  request.getRequestURI(), elapsed);
 *         alertService.sendSlowRequestAlert(request.getRequestURI(), elapsed);
 *     }
 * }
 * }</pre>
 *
 * <h2>📌 注意事项</h2>
 * <ul>
 *     <li>内存版防重复提交和限流仅适用于单机环境，分布式环境请使用Redis</li>
 *     <li>操作日志默认使用 "anonymous" 作为用户名，实际项目请集成认证系统</li>
 *     <li>重试机制会增加方法执行时间，请谨慎设置重试次数和延迟时间</li>
 *     <li>RequestLogInterceptor 会记录所有请求，注意日志文件大小</li>
 *     <li>生产环境建议将操作日志异步保存到数据库或消息队列</li>
 * </ul>
 *
 * @author Under-Utils Team
 * @version 1.0.0
 * @since 1.0.0
 */
package com.undernine.utils.spring;
