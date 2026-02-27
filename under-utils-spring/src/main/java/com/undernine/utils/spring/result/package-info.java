/**
 * 统一返回结果包
 * <p>
 * 提供 RESTful API 的统一返回格式封装，包含状态码、消息和数据。
 * </p>
 *
 * <h2>核心类</h2>
 * <ul>
 *     <li>{@link com.undernine.utils.spring.result.Result} - 统一响应结果封装类</li>
 *     <li>{@link com.undernine.utils.spring.result.ResultCode} - 响应状态码枚举</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 * <pre>{@code
 * @RestController
 * @RequestMapping("/api/users")
 * public class UserController {
 *
 *     // 成功返回（无数据）
 *     @DeleteMapping("/{id}")
 *     public Result<Void> deleteUser(@PathVariable Long id) {
 *         userService.delete(id);
 *         return Result.success();
 *     }
 *
 *     // 成功返回（有数据）
 *     @GetMapping("/{id}")
 *     public Result<User> getUser(@PathVariable Long id) {
 *         User user = userService.getById(id);
 *         return Result.success(user);
 *     }
 *
 *     // 成功返回（有数据 + 自定义消息）
 *     @PostMapping
 *     public Result<User> createUser(@RequestBody User user) {
 *         User created = userService.create(user);
 *         return Result.success(created, "用户创建成功");
 *     }
 *
 *     // 失败返回
 *     @GetMapping("/check/{username}")
 *     public Result<Void> checkUsername(@PathVariable String username) {
 *         if (userService.existsByUsername(username)) {
 *             return Result.fail("用户名已存在");
 *         }
 *         return Result.success();
 *     }
 *
 *     // 使用预定义状态码
 *     @GetMapping("/validate/{id}")
 *     public Result<Void> validateUser(@PathVariable Long id) {
 *         if (!userService.exists(id)) {
 *             return Result.fail(ResultCode.DATA_NOT_FOUND);
 *         }
 *         return Result.success();
 *     }
 * }
 * }</pre>
 *
 * <h2>返回格式</h2>
 * <pre>{@code
 * // 成功响应
 * {
 *   "code": 200,
 *   "message": "操作成功",
 *   "data": { ... },
 *   "timestamp": 1706169600000
 * }
 *
 * // 失败响应
 * {
 *   "code": 400,
 *   "message": "参数错误",
 *   "timestamp": 1706169600000
 * }
 * }</pre>
 *
 * <h2>状态码规范</h2>
 * <ul>
 *     <li>2xx - 成功状态码（200 成功，201 已创建）</li>
 *     <li>4xx - 客户端错误（400 参数错误，401 未授权，403 无权限，404 资源不存在）</li>
 *     <li>5xx - 服务端错误（500 服务器内部错误，503 服务暂不可用）</li>
 *     <li>10000+ - 业务错误码（10000 业务异常，10001 数据不存在，10002 数据已存在）</li>
 * </ul>
 *
 * <h2>注意事项</h2>
 * <ul>
 *     <li>建议在全局异常处理器中统一返回 Result 格式</li>
 *     <li>可根据业务需要扩展 ResultCode 枚举</li>
 *     <li>Result 类使用 @JsonInclude(NON_NULL) 避免返回 null 字段</li>
 *     <li>timestamp 字段自动填充当前时间戳</li>
 * </ul>
 *
 * @author deng
 * @version 1.0.0
 * @since 1.0.0
 */
package com.undernine.utils.spring.result;
