package piglin.swapswap.domain.post.constant;

import lombok.Getter;

@Getter
public enum Category {

    HOME_ELECTRONICS(CategoryName.HOME_ELECTRONICS),

    ELECTRONICS(CategoryName.ELECTRONICS),

    FASHION_MISCELLANEOUS(CategoryName.FASHION_MISCELLANEOUS),

    BEAUTY(CategoryName.BEAUTY),

    INFANT_CHILDCARE(CategoryName.INFANT_CHILDCARE),

    FOOD(CategoryName.FOOD),

    KITCHEN(CategoryName.KITCHEN),

    HOUSEHOLD(CategoryName.HOUSEHOLD),

    FURNITURE_INTERIOR(CategoryName.FURNITURE_INTERIOR),

    SPORT_LEISURE(CategoryName.SPORT_LEISURE),

    CAR(CategoryName.CAR),

    HOBBY_GAME_BOOK_ALBUM_DVD(CategoryName.HOBBY_GAME_BOOK_ALBUM_DVD),

    OFFICE_SUPPLIES(CategoryName.OFFICE_SUPPLIES),

    PET(CategoryName.PET),

    ETC(CategoryName.ETC);

    private final String name;

    Category(String name) {
        this.name = name;
    }

    public static class CategoryName {

        public static final String HOME_ELECTRONICS = "가전제품";
        public static final String ELECTRONICS = "전자제품";
        public static final String FASHION_MISCELLANEOUS = "패션의류/잡화";
        public static final String BEAUTY = "뷰티/미용";
        public static final String INFANT_CHILDCARE = "출산/유아동";
        public static final String FOOD = "식품";
        public static final String KITCHEN = "주방용품";
        public static final String HOUSEHOLD = "생활용품";
        public static final String FURNITURE_INTERIOR = "가구/인테리어";
        public static final String SPORT_LEISURE = "스포츠/레저";
        public static final String CAR = "자동차용품";
        public static final String HOBBY_GAME_BOOK_ALBUM_DVD = "취미/게임/도서/음반/DVD";
        public static final String OFFICE_SUPPLIES = "문구/오피스";
        public static final String PET = "반려동물용품";
        public static final String ETC = "기타중고물품";
    }
}
