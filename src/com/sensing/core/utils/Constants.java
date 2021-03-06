package com.sensing.core.utils;

import java.util.HashMap;
import java.util.Map;

import com.sensing.core.utils.props.PropUtils;

@SuppressWarnings("all")
public class Constants {

    // 图片服务请求地址
    public final static String SERVER_URL_PHOTO = PropUtils.getString("remote.img.url");
    // 已抓拍图片的以图搜图的默认阈值
    public final static Float PHOTO_SEARCH_SCORE = 60.00f;
    // 比对服务超时时间
    public final static Integer CMP_SERVER_TIMEOUT = 30000;
    // 比对服务地址
    public final static String CMP_SERVER_URL = "http://" + PropUtils.getString("cmp.server.ip") + ":"
            + PropUtils.getString("cmp.server.port") + PropUtils.getString("cmp.server.addr");
    // 获取特征文件的服务地址
    public final static String CMP_FEATURE_URL = "http://" + PropUtils.getString("cmp.server.ip") + ":"
            + PropUtils.getString("cmp.server.port") + PropUtils.getString("cmp.feature.addr");

    // 逻辑删除标记
    public final static Short DELETE_NO = 0; // 未删除
    public final static Short DELETE_YES = 1; // 已删除

    // 用户状态
    public final static Integer USER_STATE_USE = 1; // 启用
    public final static Integer USER_STATE_UNUSE = 0; // 停用

    // 任务状态
    public final static Integer JOBS_STATE_CLOSED = 0; // 关闭
    public final static Integer JOBS_STATE_USE = 1; // 布控中

    // 存放excel的文件夹名称
    public final static String EXPORT_EXCEL_FILENAME = "file";
    public final static String WEB_DOWNLAOD_URL = PropUtils.getString("web.download.url");
    // 项目名称
    public final static String PROJECT_NAME = "videostructure";
    // 项目根路径
    public final static String PROJECT_ROOT = "ROOT";

    // 订阅类型
    public final static Integer SUBSCRIBE_TYPE_ORIGINAL = 1; // 告警
    public final static Integer SUBSCRIBE_TYPE_SEC = 2; // 报警

    // 抓拍状态
    public final static Integer CAP_STAT_CLOSE = 0; // 关闭
    public final static Integer CAP_STAT_OPEN = 1; // 开启
    public final static Integer CAP_STAT_EXCEPTION = 2;// 异常
    public final static Map<Integer, String> CAP_STAT_MAP = new HashMap<Integer, String>() {
        {
            put(CAP_STAT_CLOSE, "关闭");
            put(CAP_STAT_OPEN, "打开");
            put(CAP_STAT_EXCEPTION, "异常");
            put(null, "异常");
        }
    };

    public static Integer CHANNEL_ORIGIN_PLATFORM = 1; // 来源：平台
    public static Integer CHANNEL_ORIGIN_OTHER = 2; // 来源：其它

    // 接口调用方式
    public static String INTERFACE_CALL_TYPE_INIT = "主动";
    public static String INTERFACE_CALL_TYPE_PASS = "被动";

    // 服务模块
    public static String SEVICE_MODEL_ZPFW = "抓拍服务";
    public static String SEVICE_MODEL_BDSF = "比对算法";
    public static String SEVICE_MODEL_GJFW = "告警服务";
    public static String SEVICE_MODEL_BDFW = "比对服务";
    public static String SEVICE_MODEL_SYNCH = "同步服务";
    public static String SEVICE_MODEL_TASK = "任务定时服务";
    public static String SEVICE_MODEL_STATICVIDEO = "离线视频服务";

    // 通道类型
    public static Integer CHANNEL_TYPE_VIDEO = 0;// 视频平台
    public static Integer CHANNEL_TYPE_RTSP = 1;// rtsp视频流

    // 系统用户的角色
//    1:管理员  2:系统用户  3:业务用户 4:超级管理员
    public static Integer SYS_ROLE_ADMIN = 1;// 管理员
    public static Integer SYS_ROLE_SYSUSER = 2;// 系统用户
    public static Integer SYS_ROLE_BUSINESS = 3;// 业务用户
    public static Integer SYS_ROLE_SUPERADMIN = 4;// 超级管理员

    // 任务task对应的状态值 0:待启动(任务从未进行) 1:处理中 3:已暂停 4:已停止 5:已完成 6:失败
    public final static Integer TASK_STAT_WAITSTART = 0; // 待启动(任务从未进行)
    public final static Integer TASK_STAT_RUNNING = 1; // 处理中
    public final static Integer TASK_STAT_INREST = 2; // 休息中
    public final static Integer TASK_STAT_PAUSE = 3; // 已暂停,一期暂无
    public final static Integer TASK_STAT_STOP = 4; // 已停止,一期暂无
    public final static Integer TASK_STAT_DONE = 5; // 已完成
    public final static Integer TASK_STAT_FAILEE = 6; // 失败,一期暂无
    public final static Map<Integer, String> TASK_STAT_MAP = new HashMap<Integer, String>() {
        {
            put(TASK_STAT_WAITSTART, "待启动");
            put(TASK_STAT_RUNNING, "处理中");
            put(TASK_STAT_INREST, "休息中");
            put(TASK_STAT_PAUSE, "已暂停");
            put(TASK_STAT_STOP, "已停止");
            put(TASK_STAT_DONE, "已完成");
            put(TASK_STAT_FAILEE, "失败");

        }
    };

    // 抓拍识别的类型 1：行人   2：机动车   4：非机动车
    public final static Integer CAP_ANALY_TYPE_PERSON = 1;// 行人
    public final static Integer CAP_ANALY_TYPE_MOTOR_VEHICLE = 3;// 机动车
    public final static Integer CAP_ANALY_TYPE_NONMOTOR_VEHICLE = 4;// 非机动车
    public final static Map<Integer, String> CAP_ANALY_TYPE = new HashMap<Integer, String>() {
        {
            put(CAP_ANALY_TYPE_PERSON, "行人");
            put(CAP_ANALY_TYPE_MOTOR_VEHICLE, "机动车");
            put(CAP_ANALY_TYPE_NONMOTOR_VEHICLE, "非机动车");

        }
    };

    // 车辆模版库识别的类型 1：重点车辆信息库   2：被盗抢车辆库   3：涉案车辆信息库
    public final static Integer AOI_VEHICLE_DEPOT = 1;// 重点车辆信息库
    public final static Integer ROBBERY_VEHICLE_DEPOT = 2;// 被盗抢车辆库
    public final static Integer INVOLVED_VEHICLE_DEPOT = 3;// 涉案车辆信息库
    public final static Map<Integer, String> VEHICLE_DEPOT_ANALY_TYPE = new HashMap<Integer, String>() {
        {
            put(AOI_VEHICLE_DEPOT, "重点车辆信息库");
            put(ROBBERY_VEHICLE_DEPOT, "被盗抢车辆库");
            put(INVOLVED_VEHICLE_DEPOT, "涉案车辆信息库");
        }
    };

    // 1:实时分析的任务 2：分时段分析的任务 3：离线视频任务
    public final static Integer TASK_TYPE_REALTime = 1;// 实时分析的任务
    public final static Integer TASK_TYPE_DAYPARTING = 2;// 分时段分析的任务
    public final static Integer TASK_TYPE_OFFLINE = 3;// 离线视频任务
    public final static Map<Integer, String> TASK_TYPE = new HashMap<Integer, String>() {
        {
            put(TASK_TYPE_REALTime, "实时分析");
            put(TASK_TYPE_DAYPARTING, "分时段分析");
            put(TASK_TYPE_OFFLINE, "离线视频");

        }
    };

    // mongo集合
    public final static String PERSON = "Person";
    public final static String MOTOR_VEHICLE = "MotorVehicle";
    public final static String NONMOTOR_VEHICLE = "NonmotorVehicle";
    public final static Map<Integer, String> CAP_TYPE_MAP = new HashMap<Integer, String>() {
        {
            put(CAP_ANALY_TYPE_PERSON, PERSON);
            put(CAP_ANALY_TYPE_MOTOR_VEHICLE, MOTOR_VEHICLE);
            put(CAP_ANALY_TYPE_NONMOTOR_VEHICLE, NONMOTOR_VEHICLE);
        }
    };

    // 系统参数
    public final static String SYS_TIMESYN = "TIME_SYN";
    public final static String SYS_TYPECODE_TIMEZONES = "SYS_SYN_TIMEZONES";


    //布控状态状态 10:待启动 20:布控中  30:暂停中 40:休息中 50:已撤控  60:撤控中 70:已完成
    public final static Integer JOB_STATE_WAITSTART = 10;// 待开启
    public final static Integer JOB_STATE_RUNNING = 20;// 布控中
    public final static Integer JOB_STATE_PAUSE = 30;// 暂停中
    public final static Integer JOB_STATE_HADTAKENCONTROL = 40;// 已撤控
    public final static Integer JOB_STATE_INTAKENCONTROL = 50;// 撤控中
    public final static Integer JOB_STATE_DONE = 60;// 已完成
    public final static Integer JOB_STATE_INREST = 70;// 休息中


    public final static Map<Integer, String> JOB_STATE_MAP = new HashMap<Integer, String>() {
        {
            put(JOB_STATE_WAITSTART, "待启动");
            put(JOB_STATE_RUNNING, "布控中");
            put(JOB_STATE_PAUSE, "暂停中");
            put(JOB_STATE_HADTAKENCONTROL, "已撤控");
            put(JOB_STATE_INTAKENCONTROL, "撤控中");
            put(JOB_STATE_DONE, "已完成");
            put(JOB_STATE_INREST, "休息中");

        }
    };

    //布控的审批权限,资源表里有更行的话，审批的id都不能变动，都是91
    public final static Integer JOB_RATIFY_QUALIFICATION = 91;

}
