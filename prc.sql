/*
Navicat MySQL Data Transfer

Source Server         : tong
Source Server Version : 80030
Source Host           : hk-cdb-dd5w6zd3.sql.tencentcdb.com:63936
Source Database       : prc

Target Server Type    : MYSQL
Target Server Version : 80030
File Encoding         : 65001

Date: 2023-10-02 21:54:38
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for d_merchant_order
-- ----------------------------
DROP TABLE IF EXISTS `d_merchant_order`;
CREATE TABLE `d_merchant_order` (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `merchant_id` int DEFAULT NULL,
  `money` decimal(19,2) DEFAULT NULL,
  `notify` bit(1) DEFAULT NULL,
  `notify_json` json DEFAULT NULL,
  `notify_url` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `order_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `pay_status` int DEFAULT NULL,
  `pay_type` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `pay_url` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `payment_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `tenant_id` int DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unique_merchant_order` (`order_id`) USING BTREE,
  KEY `index_merchant_tenant` (`tenant_id`) USING BTREE,
  KEY `index_merchant_merchant` (`merchant_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=242 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of d_merchant_order
-- ----------------------------

-- ----------------------------
-- Table structure for d_order_error_notify
-- ----------------------------
DROP TABLE IF EXISTS `d_order_error_notify`;
CREATE TABLE `d_order_error_notify` (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `end` bit(1) NOT NULL,
  `order_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `supplier` bit(1) NOT NULL,
  `res` json DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=1879 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of d_order_error_notify
-- ----------------------------

-- ----------------------------
-- Table structure for d_order_slow_slot
-- ----------------------------
DROP TABLE IF EXISTS `d_order_slow_slot`;
CREATE TABLE `d_order_slow_slot` (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `end` bit(1) DEFAULT NULL,
  `over` bit(1) DEFAULT NULL,
  `payment_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `payment_no` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `phone` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `supplier_id` int DEFAULT NULL,
  `supplier_order_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `tenant_id` int DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  KEY `index_slot_tenant` (`tenant_id`) USING BTREE,
  KEY `index_slot_supplier` (`supplier_id`) USING BTREE,
  KEY `index_slot_supplier_order` (`supplier_order_id`) USING BTREE,
  KEY `index_slot_payment` (`payment_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of d_order_slow_slot
-- ----------------------------

-- ----------------------------
-- Table structure for d_payment
-- ----------------------------
DROP TABLE IF EXISTS `d_payment`;
CREATE TABLE `d_payment` (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `agent` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `aisle_id` int DEFAULT NULL,
  `ext_json` json DEFAULT NULL,
  `finish_status` int DEFAULT NULL,
  `finish_time` datetime DEFAULT NULL,
  `merchant_id` int DEFAULT NULL,
  `merchant_notify` bit(1) DEFAULT NULL,
  `merchant_order_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `money` decimal(19,2) DEFAULT NULL,
  `monitor` bigint DEFAULT NULL,
  `monitor_bean` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `operator` int DEFAULT NULL,
  `pay_bean` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `pay_status` int DEFAULT NULL,
  `pay_time` datetime DEFAULT NULL,
  `pay_type` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `pay_url` text CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci,
  `payment_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `payment_no` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `primary` bit(1) DEFAULT NULL,
  `product_no` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `province_id` int DEFAULT NULL,
  `province_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `proxy_ip` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `query_json` json DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `slow` bit(1) DEFAULT NULL,
  `supplier_id` int DEFAULT NULL,
  `supplier_notify` bit(1) DEFAULT NULL,
  `supplier_order_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `tenant_id` int DEFAULT NULL,
  `user_ip` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `user_key` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `wait` bigint DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unique_payment_payment` (`payment_id`) USING BTREE,
  KEY `index_payment_tenant` (`tenant_id`) USING BTREE,
  KEY `index_payment_merchant` (`merchant_id`) USING BTREE,
  KEY `index_payment_supplier` (`supplier_id`) USING BTREE,
  KEY `inedx_payment_supplier_order` (`supplier_order_id`) USING BTREE,
  KEY `index_payment_merchant_order` (`merchant_order_id`) USING BTREE,
  KEY `index_payment_product` (`product_no`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=37910 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of d_payment
-- ----------------------------

-- ----------------------------
-- Table structure for d_supplier_order
-- ----------------------------
DROP TABLE IF EXISTS `d_supplier_order`;
CREATE TABLE `d_supplier_order` (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `ext` json DEFAULT NULL,
  `finish_date` datetime DEFAULT NULL,
  `finish_status` int DEFAULT NULL,
  `money` decimal(19,2) DEFAULT NULL,
  `notify` bit(1) DEFAULT NULL,
  `notify_json` json DEFAULT NULL,
  `notify_url` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `operator` int DEFAULT NULL,
  `order_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `pay_status` int DEFAULT NULL,
  `payment_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `product_no` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `province_id` int DEFAULT NULL,
  `province_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `slow` bit(1) DEFAULT NULL,
  `supplier_id` int DEFAULT NULL,
  `tenant_id` int DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unique_supplier_order` (`order_id`) USING BTREE,
  KEY `index_supplier_tenant` (`tenant_id`) USING BTREE,
  KEY `index_supplier_payment` (`payment_id`) USING BTREE,
  KEY `index_supplier_supplier` (`supplier_id`) USING BTREE,
  KEY `index_supplier_product` (`product_no`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=101778 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of d_supplier_order
-- ----------------------------

-- ----------------------------
-- Table structure for d_supplier_order_log
-- ----------------------------
DROP TABLE IF EXISTS `d_supplier_order_log`;
CREATE TABLE `d_supplier_order_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `identity_id` int DEFAULT NULL,
  `order_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `product_no` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `req` json DEFAULT NULL,
  `res` json DEFAULT NULL,
  `tenant_id` int DEFAULT NULL,
  `type` int DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=234006 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of d_supplier_order_log
-- ----------------------------

-- ----------------------------
-- Table structure for m_ali_shop
-- ----------------------------
DROP TABLE IF EXISTS `m_ali_shop`;
CREATE TABLE `m_ali_shop` (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `ip_json` varchar(255) DEFAULT NULL,
  `login_time` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of m_ali_shop
-- ----------------------------

-- ----------------------------
-- Table structure for m_ali_shop_goods
-- ----------------------------
DROP TABLE IF EXISTS `m_ali_shop_goods`;
CREATE TABLE `m_ali_shop_goods` (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `number` int DEFAULT NULL,
  `path` varchar(255) DEFAULT NULL,
  `shop_id` int DEFAULT NULL,
  `status` bit(1) DEFAULT NULL,
  `success_number` int DEFAULT NULL,
  `token` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

-- ----------------------------
-- Records of m_ali_shop_goods
-- ----------------------------

-- ----------------------------
-- Table structure for m_mount_log
-- ----------------------------
DROP TABLE IF EXISTS `m_mount_log`;
CREATE TABLE `m_mount_log` (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `order_id` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `product_no` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `req` json DEFAULT NULL,
  `res` json DEFAULT NULL,
  `type` int DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=399 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of m_mount_log
-- ----------------------------

-- ----------------------------
-- Table structure for s_aisle
-- ----------------------------
DROP TABLE IF EXISTS `s_aisle`;
CREATE TABLE `s_aisle` (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `before` bit(1) DEFAULT NULL,
  `before_bean_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `fix` bit(1) NOT NULL,
  `monitor_bean` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `monitoring_long` bigint DEFAULT NULL,
  `mount_sleep` bigint DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `operators` json DEFAULT NULL,
  `pay_bean_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `pay_type` json DEFAULT NULL,
  `primary` bit(1) DEFAULT NULL,
  `recharge_money` json DEFAULT NULL,
  `slow` bit(1) DEFAULT NULL,
  `status` bit(1) DEFAULT NULL,
  `bank_long` bigint DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unique_aisle_name` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of s_aisle
-- ----------------------------

-- ----------------------------
-- Table structure for s_authority
-- ----------------------------
DROP TABLE IF EXISTS `s_authority`;
CREATE TABLE `s_authority` (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `authority` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `has_role` bit(1) DEFAULT NULL,
  `hide` bit(1) DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `sort` int DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unique_auth_name` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of s_authority
-- ----------------------------
INSERT INTO `s_authority` VALUES ('1', null, '2023-08-20 00:50:22', 'manager', '', '\0', 'manager', '1');
INSERT INTO `s_authority` VALUES ('2', '2023-08-20 00:50:22', '2023-08-20 00:50:22', 'public', '\0', '\0', 'public', '2');
INSERT INTO `s_authority` VALUES ('3', '2023-08-20 00:50:22', '2023-08-20 00:50:22', 'tenant', '', '\0', 'tenant', '3');
INSERT INTO `s_authority` VALUES ('4', '2023-08-20 00:50:22', '2023-08-20 00:50:22', 'supplier', '', '\0', 'supplier', '4');
INSERT INTO `s_authority` VALUES ('11', '2023-08-20 00:50:22', '2023-08-20 00:50:22', 'merchant', '', '\0', 'merchant', '5');

-- ----------------------------
-- Table structure for s_dict
-- ----------------------------
DROP TABLE IF EXISTS `s_dict`;
CREATE TABLE `s_dict` (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `major_key` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `val` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unique_dick_key` (`major_key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of s_dict
-- ----------------------------
INSERT INTO `s_dict` VALUES ('1', null, null, 'money', null, '10,20,30,50,100,200,300,500');
INSERT INTO `s_dict` VALUES ('2', null, null, 'cashier', null, 'http://pingdtr.xyz/505340822c7e43de836f750d5f1261b9/#/cashier?tradeId=%s');
INSERT INTO `s_dict` VALUES ('3', null, null, 'kaka', null, '{\"create_url\":\"http://pdapi.panda763.com:1338/api/order/create\",\"id\":\"100608\"}');

-- ----------------------------
-- Table structure for s_menu
-- ----------------------------
DROP TABLE IF EXISTS `s_menu`;
CREATE TABLE `s_menu` (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `authority` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `component` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `hide` bit(1) NOT NULL,
  `icon` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `menu_type` int DEFAULT NULL,
  `parent_id` int DEFAULT NULL,
  `path` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `sort_number` int DEFAULT NULL,
  `target` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `title` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of s_menu
-- ----------------------------
INSERT INTO `s_menu` VALUES ('1', null, '2023-08-20 13:58:53', 'manager', '', '\0', 'el-icon-s-tools', '0', '13', '/manager/system', '2', '_self', '系统设置');
INSERT INTO `s_menu` VALUES ('2', '2023-08-20 13:58:53', '2023-08-20 13:58:53', 'manager', '/manager/system/gateway/index', '\0', 'el-icon-s-unfold', '0', '1', '/manager/system/gateway/index', '1', '_self', '网关设置');
INSERT INTO `s_menu` VALUES ('3', '2023-08-20 13:58:53', '2023-08-20 13:58:53', 'manager', '/manager/system/menu/index', '\0', 'el-icon-s-order', '0', '1', '/manager/system/menu/index', '2', '_self', '菜单设置');
INSERT INTO `s_menu` VALUES ('4', '2023-08-20 13:58:53', '2023-08-20 13:58:53', 'manager', '/manager/system/auth/index', '\0', 'el-icon-s-promotion', '0', '1', '/manager/system/auth/index', '3', '_self', '权限设置');
INSERT INTO `s_menu` VALUES ('5', '2023-08-20 13:58:53', '2023-08-20 13:58:53', 'manager', '/manager/system/role/index', '\0', 'el-icon-s-check', '0', '1', '/manager/system/role/index', '4', '_self', '角色设置');
INSERT INTO `s_menu` VALUES ('6', null, '2023-08-20 14:15:50', 'manager', '', '\0', 'el-icon-_user-group', '0', '13', '/manager/user', '3', '_self', '用户管理');
INSERT INTO `s_menu` VALUES ('7', null, '2023-08-20 14:18:25', 'manager', '/manager/user/tenant/index', '\0', 'el-icon-_bug', '0', '6', '/manager/user/tenant/index', '1', '_self', '租户管理');
INSERT INTO `s_menu` VALUES ('10', null, '2023-08-20 14:22:33', 'manager', '', '\0', 'el-icon-_nav', '0', '13', '/manager/indent', '4', '_self', '订单管理');
INSERT INTO `s_menu` VALUES ('11', '2023-08-20 14:23:14', '2023-08-20 14:23:14', 'manager', '/manager/indent/merchant/index', '\0', 'el-icon-s-fold', '0', '10', '/manager/indent/merchant/index', '1', '_self', '商户订单');
INSERT INTO `s_menu` VALUES ('12', '2023-08-20 14:24:23', '2023-08-20 14:24:23', 'manager', '/manager/indent/payment/index', '\0', 'el-icon-s-unfold', '0', '10', '/manager/indent/payment/index', '2', '_self', '支付单');
INSERT INTO `s_menu` VALUES ('13', null, null, 'manager', '', '', 'el-icon-s-unfold', '0', '0', '/manager', '0', '_self', '超管菜单');
INSERT INTO `s_menu` VALUES ('14', null, '2023-08-20 14:30:48', 'manager', '/manager/indent/supplier/index', '\0', 'el-icon-refresh', '0', '10', '/manager/indent/supplier/index', '3', '_self', '供货单');
INSERT INTO `s_menu` VALUES ('15', null, '2023-08-20 14:31:44', 'manager', '', '\0', 'el-icon-_more', '0', '13', '/manager/aisle', '5', '_self', '通道管理');
INSERT INTO `s_menu` VALUES ('16', null, '2023-08-20 14:32:27', 'manager', '/manager/aisle/list/index', '\0', 'el-icon-data-board', '0', '15', '/manager/aisle/list/index', '0', '_self', '通道列表');
INSERT INTO `s_menu` VALUES ('17', '2023-08-20 14:34:01', '2023-08-20 14:34:01', 'tenant', '', '', 'el-icon-_nav', '0', '0', '/tenant', '2', '_self', '租户菜单');
INSERT INTO `s_menu` VALUES ('18', null, '2023-08-20 14:34:54', 'tenant', '', '\0', 'el-icon-switch-button', '0', '17', '/tenant/supplier', '2', '_self', '供货商管理');
INSERT INTO `s_menu` VALUES ('19', '2023-08-20 14:35:39', '2023-08-20 14:35:39', 'tenant', '/tenant/supplier/index', '\0', 'el-icon-time', '0', '18', '/tenant/supplier/index', '2', '_self', '供货商列表');
INSERT INTO `s_menu` VALUES ('20', '2023-08-20 14:36:10', '2023-08-20 14:36:10', 'tenant', '', '\0', 'el-icon-loading', '0', '17', '/tenant/merchant', '3', '_self', '商户管理');
INSERT INTO `s_menu` VALUES ('21', '2023-08-20 14:36:47', '2023-08-20 14:36:47', 'tenant', '/tenant/merchant/index', '\0', 'el-icon-zoom-in', '0', '20', '/tenant/merchant/index', '4', '_self', '商户列表');
INSERT INTO `s_menu` VALUES ('22', '2023-08-20 14:37:30', '2023-08-20 14:37:30', 'tenant', '', '\0', 'el-icon-folder', '0', '17', '/tenant/aisle', '4', '_self', '通道管理');
INSERT INTO `s_menu` VALUES ('23', '2023-08-20 14:38:03', '2023-08-20 14:38:03', 'tenant', '/tenant/aisle/index', '\0', 'el-icon-zoom-in', '0', '22', '/tenant/aisle/index', '0', '_self', '通道列表');
INSERT INTO `s_menu` VALUES ('24', '2023-08-20 14:38:32', '2023-08-20 14:38:32', 'tenant', '', '\0', 'el-icon-_nav', '0', '17', '/tenant/indent', '5', '_self', '订单管理');
INSERT INTO `s_menu` VALUES ('25', '2023-08-20 14:39:10', '2023-08-20 14:39:10', 'tenant', '/tenant/indent/merchant/index', '\0', 'el-icon-s-fold', '0', '24', '/tenant/indent/merchant/index', '1', '_self', '商户订单');
INSERT INTO `s_menu` VALUES ('26', '2023-08-20 14:39:49', '2023-08-20 14:39:49', 'tenant', '/tenant/indent/payment/index', '\0', 'el-icon-s-unfold', '0', '24', '/tenant/indent/payment/index', '2', '_self', '支付单');
INSERT INTO `s_menu` VALUES ('27', '2023-08-20 14:40:31', '2023-08-20 14:40:31', 'tenant', '/tenant/indent/supplier/index', '\0', 'el-icon-s-unfold', '0', '24', '/tenant/indent/supplier/index', '4', '_self', '供货单');
INSERT INTO `s_menu` VALUES ('28', '2023-08-20 14:41:35', '2023-08-20 14:41:35', 'manager', '/manager/aisle/paytype/index', '\0', 'el-icon-_fly', '0', '15', '/manager/aisle/paytype/index', '2', '_self', '支付方式列表');
INSERT INTO `s_menu` VALUES ('29', null, '2023-08-30 17:25:31', 'manager', '', '\0', 'el-icon-star-off', '0', '13', '/manager/home', '1', '_self', 'Home');
INSERT INTO `s_menu` VALUES ('30', '2023-08-30 17:27:28', '2023-08-30 17:27:28', 'manager', '/manager/home/tab', '\0', 'el-icon-star-off', '0', '29', '/manager/home/tab', '2', '_self', 'Tab');
INSERT INTO `s_menu` VALUES ('31', '2023-08-30 17:27:52', '2023-08-30 17:27:52', 'manager', '/manager/home/static', '\0', 'el-icon-star-off', '0', '29', '/manager/home/static', '1', '_self', 'Static');
INSERT INTO `s_menu` VALUES ('32', null, '2023-08-31 10:55:13', 'tenant', '/tenant/home', '\0', 'el-icon-_heart', '0', '17', '/tenant/home', '1', '_self', 'Home');
INSERT INTO `s_menu` VALUES ('33', '2023-09-11 16:46:12', '2023-09-11 16:46:12', 'manager', '/manager/aisle/voucher', '\0', 'el-icon-_info', '0', '15', '/manager/aisle/voucher', '3', '_self', '凭证模板');

-- ----------------------------
-- Table structure for s_mount
-- ----------------------------
DROP TABLE IF EXISTS `s_mount`;
CREATE TABLE `s_mount` (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `hide` bit(1) DEFAULT NULL,
  `mount_code` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unique_mount_name` (`name`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of s_mount
-- ----------------------------

-- ----------------------------
-- Table structure for s_pay_type
-- ----------------------------
DROP TABLE IF EXISTS `s_pay_type`;
CREATE TABLE `s_pay_type` (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `pay_key` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `pay_type` int DEFAULT NULL,
  `remark` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `mate` int DEFAULT NULL,
  `status` bit(1) NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unique_paytype_key` (`pay_key`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of s_pay_type
-- ----------------------------

-- ----------------------------
-- Table structure for s_proxy
-- ----------------------------
DROP TABLE IF EXISTS `s_proxy`;
CREATE TABLE `s_proxy` (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `account` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `config` json DEFAULT NULL,
  `proxy_type` int DEFAULT NULL,
  `status` bit(1) DEFAULT NULL,
  `type` int DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of s_proxy
-- ----------------------------

-- ----------------------------
-- Table structure for s_role
-- ----------------------------
DROP TABLE IF EXISTS `s_role`;
CREATE TABLE `s_role` (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `authority` json DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `role_key` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `sort` int DEFAULT NULL,
  `status` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unique_role_name` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of s_role
-- ----------------------------
INSERT INTO `s_role` VALUES ('1', null, null, '[1]', 'manager', 'manager', '1');
INSERT INTO `s_role` VALUES ('2', '2023-08-20 13:46:03', '2023-08-20 13:46:03', '[3]', 'tenant', 'tenant', '2');
INSERT INTO `s_role` VALUES ('3', '2023-08-20 13:46:14', '2023-08-20 13:46:14', '[4]', 'supplier', 'supplier', '3');
INSERT INTO `s_role` VALUES ('4', '2023-08-20 13:46:24', '2023-08-20 13:46:24', '[11]', 'merchant', 'merchant', '4');

-- ----------------------------
-- Table structure for s_tenant
-- ----------------------------
DROP TABLE IF EXISTS `s_tenant`;
CREATE TABLE `s_tenant` (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `mount_ids` json DEFAULT NULL,
  `secret` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `status` bit(1) DEFAULT NULL,
  `uid` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unique_tenant_name` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of s_tenant
-- ----------------------------

-- ----------------------------
-- Table structure for s_user
-- ----------------------------
DROP TABLE IF EXISTS `s_user`;
CREATE TABLE `s_user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
  `authority` json DEFAULT NULL,
  `avatar` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `google_key` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `password` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `role_id` int DEFAULT NULL,
  `status` bit(1) DEFAULT NULL,
  `username` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unique_user_name` (`username`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of s_user
-- ----------------------------
INSERT INTO `s_user` VALUES ('1', null, null, '[]', 'https://cdn.eleadmin.com/20200610/avatar.jpg', null, '123456', '1', '');

-- ----------------------------
-- Table structure for s_voucher
-- ----------------------------
DROP TABLE IF EXISTS `s_voucher`;
CREATE TABLE `s_voucher` (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `status` bit(1) DEFAULT NULL,
  `v_name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `v_url` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of s_voucher
-- ----------------------------
INSERT INTO `s_voucher` VALUES ('1', '2023-09-20 10:04:06', '2023-09-20 11:24:40', '', '电信官网', 'http://pingdtr.xyz/505340822c7e43de836f750d5f1261b9/#/vouter/telecomOfficial');
INSERT INTO `s_voucher` VALUES ('2', '2023-09-20 10:04:26', '2023-09-20 11:24:47', '', '移动官网', 'http://pingdtr.xyz/505340822c7e43de836f750d5f1261b9/#/vouter/mobileOst');

-- ----------------------------
-- Table structure for t_aisle
-- ----------------------------
DROP TABLE IF EXISTS `t_aisle`;
CREATE TABLE `t_aisle` (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `aisle_id` int DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `not_province` json DEFAULT NULL,
  `status` bit(1) NOT NULL,
  `tenant_id` int DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of t_aisle
-- ----------------------------

-- ----------------------------
-- Table structure for t_aisle_supplier
-- ----------------------------
DROP TABLE IF EXISTS `t_aisle_supplier`;
CREATE TABLE `t_aisle_supplier` (
  `slow` bit(1) DEFAULT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `aisle_id` int NOT NULL,
  `radio` decimal(19,2) NOT NULL,
  `supplier_id` int NOT NULL,
  `tenant_id` int NOT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unique_supplier_aisle` (`supplier_id`,`tenant_id`,`aisle_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of t_aisle_supplier
-- ----------------------------

-- ----------------------------
-- Table structure for t_merchant
-- ----------------------------
DROP TABLE IF EXISTS `t_merchant`;
CREATE TABLE `t_merchant` (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `secret` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `status` bit(1) DEFAULT NULL,
  `tenant_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `white_ip` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unique_merchant_name` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of t_merchant
-- ----------------------------

-- ----------------------------
-- Table structure for t_supplier
-- ----------------------------
DROP TABLE IF EXISTS `t_supplier`;
CREATE TABLE `t_supplier` (
  `id` int NOT NULL AUTO_INCREMENT,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `max_count` bigint NOT NULL,
  `name` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `produce_ips` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `repetition` bit(1) NOT NULL,
  `repetition_count` int DEFAULT NULL,
  `repetition_no` bit(1) NOT NULL,
  `secret` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  `status` bit(1) DEFAULT NULL,
  `tenant_aisle_ids` json DEFAULT NULL,
  `tenant_id` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  `username` varchar(255) CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `unique_supplier_name` (`name`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb3 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of t_supplier
-- ----------------------------
