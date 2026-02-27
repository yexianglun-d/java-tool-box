/**
 * Spring 枚举包
 * <p>
 * 提供各种业务枚举类型，用于操作类型、敏感信息类型等场景。
 * </p>
 *
 * <h2>核心枚举</h2>
 * <ul>
 *     <li>{@link com.undernine.utils.spring.enums.OperationType} - 操作类型枚举</li>
 *     <li>{@link com.undernine.utils.spring.enums.SensitiveType} - 敏感信息类型枚举</li>
 * </ul>
 *
 * <h2>使用示例</h2>
 *
 * <h3>1. 操作类型枚举</h3>
 * <pre>{@code
 * @OperationLog(
 *     module = "用户管理",
 *     type = OperationType.CREATE,  // 新增操作
 *     content = "创建用户"
 * )
 * public Result<User> createUser(User user) {
 *     return Result.success(userService.create(user));
 * }
 *
 * @OperationLog(
 *     module = "订单管理",
 *     type = OperationType.DELETE,  // 删除操作
 *     content = "删除订单"
 * )
 * public Result<Void> deleteOrder(Long id) {
 *     orderService.delete(id);
 *     return Result.success();
 * }
 * }</pre>
 *
 * <h3>2. 敏感信息类型枚举</h3>
 * <pre>{@code
 * public class User {
 *     @Sensitive(type = SensitiveType.MOBILE_PHONE)
 *     private String phone;  // 138****5678
 *
 *     @Sensitive(type = SensitiveType.ID_CARD)
 *     private String idCard; // 320***********1234
 *
 *     @Sensitive(type = SensitiveType.EMAIL)
 *     private String email;  // a***@example.com
 *
 *     @Sensitive(type = SensitiveType.BANK_CARD)
 *     private String bankCard; // 6222 **** **** 1234
 *
 *     @Sensitive(type = SensitiveType.PASSWORD)
 *     private String password; // ***
 * }
 * }</pre>
 *
 * <h2>操作类型说明</h2>
 * <ul>
 *     <li>QUERY - 查询操作</li>
 *     <li>CREATE - 新增操作</li>
 *     <li>UPDATE - 修改操作</li>
 *     <li>DELETE - 删除操作</li>
 *     <li>EXPORT - 导出操作</li>
 *     <li>IMPORT - 导入操作</li>
 *     <li>LOGIN - 登录操作</li>
 *     <li>LOGOUT - 登出操作</li>
 *     <li>OTHER - 其他操作</li>
 * </ul>
 *
 * <h2>敏感信息类型说明</h2>
 * <ul>
 *     <li>MOBILE_PHONE - 手机号：138****5678</li>
 *     <li>ID_CARD - 身份证号：320***********1234</li>
 *     <li>BANK_CARD - 银行卡号：6222 **** **** 1234</li>
 *     <li>EMAIL - 邮箱：a***@example.com</li>
 *     <li>CHINESE_NAME - 姓名：张*</li>
 *     <li>ADDRESS - 地址：北京市海淀区******</li>
 *     <li>PASSWORD - 密码：***（完全隐藏）</li>
 *     <li>FIXED_PHONE - 固定电话：010-****5678</li>
 *     <li>CAR_LICENSE - 车牌号：京A·****1</li>
 *     <li>CUSTOM - 自定义规则</li>
 * </ul>
 *
 * <h2>注意事项</h2>
 * <ul>
 *     <li>枚举类型不可修改，确保系统稳定性</li>
 *     <li>可根据业务需要扩展新的枚举值</li>
 *     <li>敏感信息脱敏规则可通过 DesensitizeUtils 自定义</li>
 * </ul>
 *
 * @author deng
 * @version 1.0.0
 * @since 1.0.0
 */
package com.undernine.utils.spring.enums;
