package piglin.swapswap.global.exception.common;

import lombok.Getter;

@Getter
public enum ErrorCode {

    // JWT
    INVALID_JWT_SIGNATURE_EXCEPTION(401, "잘못된 JWT 서명입니다."),
    EXPIRED_JWT_TOKEN_EXCEPTION(401, "만료된 JWT 토큰입니다."),
    UNSUPPORTED_JWT_TOKEN_EXCEPTION(401, "지원되지 않는 JWT 토큰입니다."),
    INVALID_JWT_EXCEPTION(401, "JWT 토큰이 잘못되었습니다"),
    NOT_REFRESH_TOKEN_EXCEPTION(401, "Refresh Token이 아닙니다."),
    NOT_MISMATCHED_REFRESH_TOKEN_EXCEPTION(401, "DB의 리프레쉬 토큰 값과 다릅니다."),
    NO_JWT_EXCEPTION(401, "이 요청은 JWT가 필요합니다."),
    NOT_SUPPORTED_GRANT_TYPE_EXCEPTION(401, "지원하지 않는 권한 부여 유형입니다."),

    // 회원
    NOT_FOUND_USER_EXCEPTION(401, "회원 정보를 찾을 수 없습니다."),
    FAILED_AUTHENTICATION_EXCEPTION(401, "인증에 실패하였습니다."),
    ALREADY_EXIST_USER_NAME_EXCEPTION(409, "이미 존재하는 이름입니다."),
    ALREADY_EXIST_EMAIL_EXCEPTION(409, "이미 존재하는 이메일입니다."),
    UNAUTHORIZED_MODIFY_EXCEPTION(401, "수정할 권한이 없습니다."),
    NO_AUTHORIZATION_EXCEPTION(400, "접근 권한이 없습니다"),
    MISMATCHED_PASSWORD_EXCEPTION(401, "비밀번호가 일치하지 않습니다."),
    FAILED_LOGIN_EXCEPTION(401, "닉네임 또는 패스워드를 확인해주세요."),
    FAILED_DELETE_MEMBER_CAUSE_SWAP_MONEY(400, "스왑 머니를 전부 출금 후 다시 진행해주세요."),

    // 이메일 인증
    MISMATCHED_AUTH_CODE_EXCEPTION(401, "인증번호가 일치하지 않습니다."),
    NOT_FOUND_AUTH_CODE_EXCEPTION(401, "없는 인증 번호입니다."),

    // Comment
    REJECT_MODIFIYING_POST_EXCEPTION(403, "작성자만 수정할 수 있습니다."),
    NOT_FOUND_COMMENT_EXCEPTION(401, "해당 댓글을 찾을 수 없습니다."),

    // 게시글
    NOT_FOUND_POST_EXCEPTION(401, "게시글을 찾을 수 없습니다."),
    WRITE_ONLY_USER(401, "로그인 한 유저만 게시글을 작성할 수 있습니다"),
    UP_IS_NEED_ONE_DAY(400, "게시된지 하루, 업 한지 하루 이상 된 게시글만 업 할 수 있습니다."),
    POST_ALREADY_DELETED(400, "이미 지워진 게시글입니다"),
    NO_MORE_POST_LIST(400, "더이상 불러올 게시글이 없습니다."),
    ALERADY_DELING_POST_EXCEPTION(400, "이미 진행 중인 게시글입니다."),
    CAN_NOT_UP_CAUSE_POST_DEAL_STATUS_IS_NOT_REQUESTED(400, "거래가 진행 중이거나 완료된 게시글은 업 할 수 없습니다."),

    // 채팅
    NOT_FOUND_CHATROOM_EXCEPTION(401, "채팅방을 찾을 수 없습니다."),
    NOT_CHAT_ROOM_MEMBER_EXCEPTION(401, "채팅방의 참여자가 아닙니다."),
    CHAT_ONLY_DIFFERENT_USER_EXCEPTION(401, "자신의 게시물에는 채팅하기를 할 수 없습니다."),

    // 쿠폰
    INVALID_EXPIRED_TIME_EXCEPTION(401, "만료 시간은 현재 시간보다 이전 시간일 수 없습니다."),
    NOT_FOUND_COUPON_EXCEPTION(401, "쿠폰을 찾을 수 없습니다."),
    INVALID_COUPON_EXCEPTION(401, "이미 모두 소진된 쿠폰입니다."),
    NOT_FOUND_MEMBER_COUPON_EXCEPTION(400, "해당 쿠폰을 소지하고 있지 않습니다."),

    // 거래
    BOTH_POST_ID_LIST_EMPTY_EXCEPTION(400, "두명의 사용자 중 적어도 한명의 사용자는 물건을 등록해야 합니다."),
    CAN_NOT_UPDATE_ALLOW_STATUS(401, "수락 상태를 수정 할 수 없는 상태입니다."),
    NOT_FOUND_DEAL_EXCEPTION(404, "거래를 찾을 수 없습니다."),
    REQUEST_ONLY_DIFFERENT_USER_EXCEPTION(401, "자신의 게시물에는 거래요청을 할 수 없습니다."),
    IS_NOT_MY_COUPON(400, "본인의 쿠폰만 사용할 수 있습니다."),
    CAN_NOT_UPDATE_POST_STATUS(401, "게시물이 이미 거래 진행 중 입니다."),
    CAN_NOT_UPDATE_CAUSE_DEAL_IS_NOT_REQUESTED(401, "이미 거래가 진행돼서 수정 할 수 없습니다."),
    DUPLICATE_COUPON_TYPE_EXCEPTION(400, "같은 타입의 쿠폰은 중복 사용이 불가능합니다."),
    UNAUTHORIZED_MODIFY_DEAL_EXCEPTION(400, "해당 거래 내용을 수정할 권한이 없습니다."),
    NOT_CONTAIN_DEAL_MEMBER_EXCEPTION(400, "자신이 포함된 거래만 볼수 있습니다."),

    // Bill
    NOT_FOUND_BILL_EXCEPTION(404, "거래 명세서를 찾을 수 없습니다"),
    CAN_NOT_UPDATE_BILL_POST_LIST_CAUSE_IS_NOT_REQUESTED(400, "요청 상태가 아니어서 수정할 수 없습니다."),

    // 지갑
    LACK_OF_SWAP_MONEY_EXCEPTION(400, "내 지갑의 스왑머니가 부족하여 출금할 수 없습니다."),
    NOT_FOUND_DEAL_WALLET_EXCEPTION(400, "거래 지갑을 찾을 수 없습니다."),

    // S3
    IS_NOT_IMAGE(400, "이미지 파일만 업로드 할 수 있습니다."),
    FAIL_UPLOAD(400, "이미지 업로드에 실패했습니다."),
    POST_IMAGE_MAX_SIZE(400, "이미지는 최대 10장만 업로드 할 수 있습니다."),
    POST_IMAGE_MIN_SIZE(400, "이미지를 최소 1장 업로드 해야합니다."),

    // 알림
    NOT_EXIST_NOTIFICATION(400, "존재하지 않는 알림입니다.");

    private final int status;

    private final String message;

    ErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
