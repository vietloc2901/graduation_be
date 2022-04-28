package locnv.haui.commons;



public class AppConstants {

    public static final String EXTENSION_DOC = ".doc";
    public static final String EXTENSION_XLSX = "xlsx";
    public static final String EXTENSION_XLS = "xls";
    public static final String COMMA_DELIMITER = ",";
    public static final String TMP_DIR = System.getProperty("java.io.tmpdir");
    public static final String ALIGN_LEFT = "LEFT";
    public static final String ALIGN_RIGHT = "RIGHT";
    public static final String STRING = "STRING";
    public static final String NUMBER = "NUMBER";
    public static final String LIST = "LIST";
    public static final String DOUBLE = "DOUBLE";
    public static final String ARRAY = "ARRAY";
    public static final String ERRORS = "ERRORS";
    public static final String CENTER = "CENTER";
    public static final String NO = "NO";

    public static final String SORT_ASC = "ASC";
    public static final String SORT_DESC = "DESC";
    public static final String FORMULA_NUMBER = "FORMULA_NUMBER";
    public static final String YYYY_MM = "yyyy/MM";
    public static final String YYYY = "yyyy";
    public static final String YYYY_MM_DD = "yyyy/MM/dd";
    public static final String DD_MM_YYYY = "dd/MM/yyyy";
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy/MM/dd HH:mm:ss";
    public static final String MM_DD = "MMdd";
    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String YYYYMM = "yyyyMM";
    public static final String YYYYMMDDHHSS = "yyyyMMddHHss";
    public static final String CONTENT_DISPOSITION = "Content-Disposition";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String ATTACHMENT_FILENAME = "attachment; filename=%s";
    public static final String ENCODING_UTF8 = "UTF-8";
    public static final String DOT = ".";
    public static final String DASH = "-";
    public static final String DASH_T = " - ";
    public static final String MIME_TYPE_XLSX = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet; charset=UTF-8";
    public static final String MIME_TYPE_XLS = "application/octet-stream";
    public static final String IMAGE_PNG = "image/png";
    public static final String NEXT_LINE = "\n";
    public static final Long EXPORT_TEMPLATE = 0l;
    public static final Long EXPORT_DATA = 1l;
    public static final Long EXPORT_ERRORS = 2l;
    public static final Long IMPORT_INSERT = 0l;
    public static final Long IMPORT_UPDATE = 1l;
    public static final String REGEX_VN = "^[0-9A-Za-z`!@#$%^&*()<>\\?\"\\]\\[|'~_:,.+-={}]{1,250}$";
    public static final String REGEX_SPACE = "\\s";
    public static final String CHAR_SPACE = " ";
    public static final String CHAR_OR = "||";
    public static final String REGEX_NUMBER = "^[0-9]$";
    public static final String REGEX_EMAIL = "^(.+)@(.+)$";
    public static final String PASS_DEFAULT = "123456";
    public static final String CHAR_STAR = "*";
    public static final String CHAR_DATE = "^(0?[1-9]|[12][0-9]|3[01])[\\/\\-](0?[1-9]|1[012])[\\/\\-]\\d{4}$";
    public static final String REGEX_EMAIL_2 = "[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,4}$";

    public class DateFormat {

        public static final String YYYY_MM = "yyyy/MM";
        public static final String YYYY_MM_DD = "yyyy/MM/dd";
        public static final String MM_DD = "MM/dd";

        private DateFormat() {}
    }

    public static class StudentStatus {

        public static final Integer STUDYING = 0;
        public static final Integer RESERVE = 1;
        public static final Integer LEAVE = 2;
        public static final Integer TRANSFERRED_SCHOOL = 3;

        private StudentStatus() {}
    }

    public static class ReserveLeaveType {

        public static final Long TYPE_LEAVE = 0L;
        public static final Long TYPE_RESERVE = 1L;

        private ReserveLeaveType() {}
    }

    public static class TeacherRatingCodeStatus {

        public static final String NOT_RATE = "not_rate";
        public static final String SELF_RATE = "self_rate";
        public static final String RATED = "Rated";
        public static final String APPROVED = "Approved";
        public static final String REJECT_RATE = "NotRate";
        public static final String REJECT_APPROVE = "NotApprove";

        private TeacherRatingCodeStatus() {}
    }

    public static class ContactPrimary {

        public static final Long FIRST_CONTACT = 1L;
        public static final Long SECOND_CONTACT = 0L;

        private ContactPrimary() {}
    }

    public static class User {

        public static final Boolean ACTIVATED = true;
        public static final Boolean INACTIVATED = false;

        private User() {}
    }

    public static class Subject {

        public static final Long TYPE_ELECTIVE = 0L;
        public static final Long TYPE_REQUIRED = 1L;

        private Subject() {}
    }

    public static class RwDclStudent {

        public static final Long TYPE_REWARD = 1L;
        public static final Long TYPE_DISCIPLINE = 0L;

        private RwDclStudent() {}
    }

    public static class ApParam {

        public static final String TYPE_SCORE_RATING = "SCORE_RATING";
        public static final String TYPE_RATE = "RATE";
        public static final String TYPE_RATE_FILE = "RATE_FILE";

        private ApParam() {}
    }

    public static final Integer TRANSFER_SCHOOL = 0;
    public static final Integer TRANSFER_CLASS = 1;
    // Sample file
    public static final String SAMPLE_FILE_NAME = "DS_lophoc_ddmmyy.xls";
    public static final String SAMPLE_FILE_NAME_TKB = "DS_TKB_ddmmyy2.xls";
    public static final String SAMPLE_FILE_NAME_TKB_LA = "DS_TKB_LA.xls";
    public static final String SAMPLE_MON_HOC_THUOC_TRUONG = "DS_monhocthuoctruong.xls";
    public static final String SAMPLE_FILE_STUDENT = "DS_Hocsinh.xls";
    public static final String SAMPLE_FILE_TEACHER_RATING = "Danhgialaodong_[namhoc].doc";
    public static final String SAMPLE_FILE_TEACHING_ASSIGNMENT1 = "DS_Phanconggiangday1.xls";
    public static final String SAMPLE_FILE_TEACHING_ASSIGNMENT2 = "DS_Phanconggiangday2.xls";
    public static final String SAMPLE_FILE_TEACHING_ASSIGNMENT3 = "DS_Phanconggiangday3.xls";
    public static final String SAMPLE_FILE_TEACHING_ASSIGNMENT4 = "DS_Phanconggiangday4.xls";
    public static final String SAMPLE_FILE_TEACHING_ASSIGNMENT1LA = "DS_Phanconggiangday1LA.xls";
    public static final String SAMPLE_FILE_TEACHING_ASSIGNMENT2LA = "DS_Phanconggiangday2LA.xls";
    public static final String SAMPLE_FILE_TEACHING_ASSIGNMENT3LA = "DS_Phanconggiangday3LA.xls";
    public static final String SAMPLE_FILE_TEACHING_ASSIGNMENT4LA = "DS_Phanconggiangday4LA.xls";

    public static final String FILE_NAME_RATE = "danhgialaodong";

    public static class TeacherManagement {

        public static final Long BONUS = 1l;
        public static final Long PUNISH = 0l;

        private TeacherManagement() {}
    }

    public static class JobTransferOut {

        public static final String CODE = "JTHO_";

        private JobTransferOut() {}
    }

    public static final int MONTH_END_OF_YEAR = 12;
    public static final String RUNNABLE = "True";

    public static final String[] LIST_PHONE_NUMBER_9 = {"309", "304", "305", "302", "307"};

    public static final String[] LIST_PHONE_NUMBER_10 = {"209", "205", "202", "207"};

    public static class ValidatePhone {

        public static final Integer VALID = 0;
        public static final Integer INVALID = 3;
        public static final Integer INVALID_LENGTH_9 = 1;
        public static final Integer INVALID_LENGTH_10 = 2;

        public static final Integer MIN_MAX_LENGTH_9 = 9;
        public static final Integer MIN_MAX_LENGTH_10 = 10;

        private ValidatePhone() {}
    }

    public static class Language {
        public static final String LA = "la";
        public static final String EN = "en";
        public static final String VN = "vn";

        private Language() {}
    }

    public static final String SEMESTER = "semester";
    public static final String CLASSROOM_CODE = "classRoomCode";
    public static final String CLASS_CODE = "classCode";
    public static final String YEARS = "years";
    public static final String LIST_SEMESTER = "listSemester";
    public static final String LENGTH = "length";
    public static final String STUDENT_CODE = "studentCode";
    public static final String SCHOOL_YEAR = "schoolYear";
    public static final String USER_ID = "userId";
    public static final String START_TIME = "startTime";
    public static final String END_TIME = "endTime";
    public static final String HISTORY_CONTACT_CODE = "historyContactCode";
    public static final String FULLNAME = "fullName";
    public static final String STATUS = "status";
    public static final String HEADER = "header";

}
