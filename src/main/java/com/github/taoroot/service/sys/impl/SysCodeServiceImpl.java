package com.github.taoroot.service.sys.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.github.taoroot.common.core.utils.R;
import com.github.taoroot.config.DBName;
import com.github.taoroot.entity.sys.SysTable;
import com.github.taoroot.mapper.sys.SysCodeMapper;
import com.github.taoroot.service.sys.SysCodeService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@AllArgsConstructor
public class SysCodeServiceImpl implements SysCodeService {

    private final SysCodeMapper sysCodeMapper;


    @Override
    public R generatorCode(String name, String alias) {
        // 查询表信息
        Map<String, String> table = sysCodeMapper.queryTable(name);
        // 查询列信息
        List<Map<String, String>> columns = sysCodeMapper.queryColumns(name);
        // 生成代码
        return R.ok(generate(table, columns, alias));

    }

    @Override
    public R tableList() {
        return R.ok(sysCodeMapper.queryTableList());
    }

    // 类型转换
    public static final String[] TABLE_PREFIX = new String[]{};
    public static final String PACKAGE = "com.github";
    public static final String EMAIL = "lujiantaoxyz@outlook.com";
    public static final String AUTHOR = "zhiyi";
    public static final String MODULE_NAME = "taoroot";

    /**
     * 生成代码
     */
    public HashMap<String, String> generate(Map<String, String> table, List<Map<String, String>> columns, String alias) {
        // 配置信息
        boolean hasBigDecimal = false;
        boolean hasList = false;
        // 表信息
        SysTable tableEntity = new SysTable();
        String tableName = table.get("tableName");
        // 没有前缀
        if (!tableName.contains("_")) {
            tableName = "sys_" + tableName;
        }
        // 使用别名
        if (StringUtils.isNotEmpty(alias)) {
            tableName = alias; // 以下都改用别名
        }
        tableEntity.setTableName(tableName); // 表名称

        tableEntity.setComments(table.get("tableComment")); // 表备注
        if (StringUtils.isEmpty(table.get("tableComment"))) {
            tableEntity.setComments(tableName);
        }

        String pre = tableName.split("_")[0]; // 获取前缀
        String next = tableName.split(pre + "_")[1]; // 获取后缀

        // 表名转换成Java类名 sys_user -> SysUser
        String className = tableToJava(tableEntity.getTableName(), TABLE_PREFIX);
        tableEntity.setClassName(className);
        // sys_user -> sysUser
        tableEntity.setLowerClassName(StringUtils.uncapitalize(className));
        // 列信息
        List<SysTable.SysTableColumn> columnList = new ArrayList<>();
        for (Map<String, String> column : columns) {
            SysTable.SysTableColumn columnEntity = new SysTable.SysTableColumn();
            columnEntity.setColumnName(column.get("columnName"));
            columnEntity.setDataType(column.get("dataType"));
            columnEntity.setComments(column.get("columnComment"));
            columnEntity.setExtra(column.get("extra"));
            // 列名转换成Java属性名
            String attrName = columnToJava(columnEntity.getColumnName());
            columnEntity.setAttrName(attrName);
            columnEntity.setLowerAttrName(StringUtils.uncapitalize(attrName));
            // 列的数据类型，转换成Java类型
            String attrType = getType(columnEntity.getDataType());
            columnEntity.setAttrType(attrType);
            if (!hasBigDecimal && "BigDecimal".equals(attrType)) {
                hasBigDecimal = true;
            }
            if (!hasList && "array".equals(columnEntity.getExtra())) {
                hasList = true;
            }
            // 是否主键
            if ("PRI".equalsIgnoreCase(column.get("columnKey")) && tableEntity.getPk() == null) {
                tableEntity.setPk(columnEntity);
            }
            columnList.add(columnEntity);
        }
        tableEntity.setColumns(columnList);

        // 没主键，则第一个字段为主键
        if (tableEntity.getPk() == null) {
            tableEntity.setPk(tableEntity.getColumns().get(0));
        }

        // 设置velocity资源加载器
        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(prop);
        // 封装模板数据
        Map<String, Object> map = new HashMap<>();
        map.put("tableName", tableEntity.getTableName()); // sys_user
        map.put("comments", tableEntity.getComments()); // 备注
        map.put("pk", tableEntity.getPk()); // 主键信息
        map.put("className", tableEntity.getClassName()); // SysUser
        map.put("classname", tableEntity.getLowerClassName()); // sysUser
        map.put("preName", pre); // sys
        map.put("nextName", next); // user
        map.put("columns", tableEntity.getColumns());
        map.put("hasBigDecimal", hasBigDecimal);
        map.put("hasList", hasList);
        map.put("package", PACKAGE);
        map.put("moduleName", MODULE_NAME);
        map.put("author", AUTHOR);
        map.put("email", EMAIL);
        map.put("Dollar", "$");
        map.put("datetime", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        VelocityContext context = new VelocityContext(map);

        // 获取模板列表
        List<String> templates = getTemplates();
        HashMap<String, String> result = new HashMap<>();
        for (String template : templates) {
            // 渲染模板
            StringWriter sw = new StringWriter();
            Template tpl = Velocity.getTemplate(template, "UTF-8");
            tpl.merge(context, sw);
            String replace = template.replace("template/", "");
            replace = replace.replace(".vm", "");
            result.put(replace, sw.toString());
        }
        return result;
    }

    /**
     * 表名转换成Java类名
     */
    public static String tableToJava(String tableName, String[] tablePrefixArray) {
        if (null != tablePrefixArray && tablePrefixArray.length > 0) {
            for (String tablePrefix : tablePrefixArray) {
                if (tableName.startsWith(tablePrefix)) {
                    tableName = tableName.replaceFirst(tablePrefix, "");
                }
            }
        }
        return columnToJava(tableName);
    }

    /**
     * 列名转换成Java属性名
     */
    public static String columnToJava(String columnName) {
        return WordUtils.capitalizeFully(columnName, new char[]{'_'}).replace("_", "");
    }

    public static String camel2Underline(String line) {
        if (line == null || "".equals(line)) {
            return "";
        }
        line = String.valueOf(line.charAt(0)).toUpperCase()
                .concat(line.substring(1));
        StringBuilder sb = new StringBuilder();
        Pattern pattern = Pattern.compile("[A-Z]([a-z\\d]+)?");
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            String word = matcher.group();
            sb.append(word.toLowerCase());
            sb.append(matcher.end() == line.length() ? "" : "_");
        }
        return sb.toString();
    }

    public String getType(String key) {
        HashMap<String, String> typeMap = new HashMap<>();
        typeMap.put("tinyint", "Integer");
        typeMap.put("smallint", "Integer");
        typeMap.put("mediumint", "Integer");
        typeMap.put("int", "Integer");
        typeMap.put("integer", "Integer");
        typeMap.put("bigint", "Integer");
        typeMap.put("float", "Float");
        typeMap.put("double", "Double");
        typeMap.put("decimal", "BigDecimal");
        typeMap.put("bit", "Boolean");
        typeMap.put("char", "String");
        typeMap.put("varchar", "String");
        typeMap.put("tinytext", "String");
        typeMap.put("text", "String");
        typeMap.put("mediumtext", "String");
        typeMap.put("longtext", "String");
        typeMap.put("date", "LocalDateTime");
        typeMap.put("datetime", "LocalDateTime");
        typeMap.put("timestamp", "LocalDateTime");
        return typeMap.get(key);
    }


    public static List<String> getTemplates() {
        List<String> templates = new ArrayList<>();
        // 后端
        templates.add("template/Entity.java.vm");
        templates.add("template/Mapper.java.vm");
        templates.add("template/Mapper.xml.vm");
        templates.add("template/Service.java.vm");
        templates.add("template/ServiceImpl.java.vm");
        templates.add("template/Controller.java.vm");
        // 前端
        templates.add("template/index.vue.vm");
        templates.add("template/api.js.vm");
        // 数据库
        templates.add("template/db.sql.vm");
        return templates;
    }
}
