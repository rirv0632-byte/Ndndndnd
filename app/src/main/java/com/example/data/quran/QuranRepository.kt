package com.example.data.quran

import android.content.Context
import com.example.R
import com.example.data.model.Ayah
import com.example.data.model.BackgroundTheme
import com.example.data.model.BackgroundType
import com.example.data.model.Reciter
import com.example.data.model.Surah
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

object QuranRepository {

    val allReciters: List<Reciter> = listOf(
        Reciter(
            id = "alafasy",
            nameArabic = "مشاري راشد العفاسي",
            nameEnglish = "Mishary Rashid Alafasy",
            subpath = "Alafasy_128kbps"
        ),
        Reciter(
            id = "maher",
            nameArabic = "ماهر المعيقلي",
            nameEnglish = "Maher Al-Muaiqly",
            subpath = "Maher_AlMuaiqly_64kbps"
        ),
        Reciter(
            id = "abdulbasit",
            nameArabic = "عبد الباسط عبد الصمد (مرتل)",
            nameEnglish = "Abdulbasit Abdulsamad (Murattal)",
            subpath = "Abdul_Basit_Murattal_192kbps"
        ),
        Reciter(
            id = "dossari",
            nameArabic = "ياسر الدوسري",
            nameEnglish = "Yasser Al-Dosari",
            subpath = "Yasser_Ad-Dussary_128kbps"
        ),
        Reciter(
            id = "ghamdi",
            nameArabic = "سعد الغامدي",
            nameEnglish = "Saad Al-Ghamdi",
            subpath = "Ghamadi_40kbps"
        ),
        Reciter(
            id = "qatami",
            nameArabic = "ناصر القطامي",
            nameEnglish = "Nasser Al-Qatami",
            subpath = "Nasser_Alqatami_128kbps"
        ),
        Reciter(
            id = "hussary",
            nameArabic = "محمود خليل الحصري",
            nameEnglish = "Mahmoud Khalil Al-Hussary",
            subpath = "Husary_128kbps"
        ),
        Reciter(
            id = "ajmi",
            nameArabic = "أحمد بن علي العجمي",
            nameEnglish = "Ahmed Al-Ajmi",
            subpath = "Ahmed_ibn_Ali_al-Ajamy_128kbps_kotSimple"
        ),
        Reciter(
            id = "minshawy",
            nameArabic = "محمد صديق المنشاوي",
            nameEnglish = "Mohamed Siddiq El-Minshawi",
            subpath = "Minshawy_Murattal_128kbps"
        ),
        Reciter(
            id = "shuraym",
            nameArabic = "سعود الشريم",
            nameEnglish = "Saood Al-Shuraym",
            subpath = "Saood_ash-Shuraym_128kbps"
        ),
        Reciter(
            id = "shatri",
            nameArabic = "أبو بكر الشاطري",
            nameEnglish = "Abu Bakr Ash-Shatri",
            subpath = "Abu_Bakr_Ash-Shaatree_128kbps"
        )
    )

    val backgroundThemes: List<BackgroundTheme> = listOf(
        BackgroundTheme(
            id = "kaaba",
            nameArabic = "الكعبة المشرفة",
            nameEnglish = "Holy Kaaba",
            type = BackgroundType.DRAWABLE,
            drawableRes = R.drawable.bg_kaaba_1788545029497
        ),
        BackgroundTheme(
            id = "stars",
            nameArabic = "سماء النجوم",
            nameEnglish = "Starry Galaxy",
            type = BackgroundType.DRAWABLE,
            drawableRes = R.drawable.bg_stars_1788545046636
        ),
        BackgroundTheme(
            id = "nature",
            nameArabic = "طبيعة وشروق",
            nameEnglish = "Sunrise Valley",
            type = BackgroundType.DRAWABLE,
            drawableRes = R.drawable.bg_nature_1788545062011
        ),
        BackgroundTheme(
            id = "mosque",
            nameArabic = "مآذن وقباب",
            nameEnglish = "Mosque Domes",
            type = BackgroundType.DRAWABLE,
            drawableRes = R.drawable.bg_mosque_1788545081339
        ),
        BackgroundTheme(
            id = "rain",
            nameArabic = "قطرات المطر",
            nameEnglish = "Gentle Rain",
            type = BackgroundType.DRAWABLE,
            drawableRes = R.drawable.bg_rain_1788545103473
        ),
        BackgroundTheme(
            id = "emerald_dark",
            nameArabic = "زمرد ملكي",
            nameEnglish = "Royal Emerald",
            type = BackgroundType.GRADIENT,
            gradientColors = listOf(0xFF0F4D3BL, 0xFF06231BL, 0xFF020E0BL)
        ),
        BackgroundTheme(
            id = "midnight_gold",
            nameArabic = "أسود وذهب",
            nameEnglish = "Midnight Gold",
            type = BackgroundType.GRADIENT,
            gradientColors = listOf(0xFF2C2209L, 0xFF141004L, 0xFF050401L)
        ),
        BackgroundTheme(
            id = "deep_night",
            nameArabic = "أعماق الليل",
            nameEnglish = "Deep Night",
            type = BackgroundType.GRADIENT,
            gradientColors = listOf(0xFF0F1E2EL, 0xFF07111CL, 0xFF02070DL)
        )
    )

    // Complete index of all 114 Surahs
    val allSurahs: List<Surah> = listOf(
        Surah(1, "الفاتحة", "Al-Fatihah", 7, "مكية"),
        Surah(2, "البقرة", "Al-Baqarah", 286, "مدنية"),
        Surah(3, "آل عمران", "Ali 'Imran", 200, "مدنية"),
        Surah(4, "النساء", "An-Nisa", 176, "مدنية"),
        Surah(5, "المائدة", "Al-Ma'idah", 120, "مدنية"),
        Surah(6, "الأنعام", "Al-An'am", 165, "مكية"),
        Surah(7, "الأعراف", "Al-A'raf", 206, "مكية"),
        Surah(8, "الأنفال", "Al-Anfal", 75, "مدنية"),
        Surah(9, "التوبة", "At-Tawbah", 129, "مدنية"),
        Surah(10, "يونس", "Yunus", 109, "مكية"),
        Surah(11, "هود", "Hud", 123, "مكية"),
        Surah(12, "يوسف", "Yusuf", 111, "مكية"),
        Surah(13, "الرعد", "Ar-Ra'd", 43, "مدنية"),
        Surah(14, "إبراهيم", "Ibrahim", 52, "مكية"),
        Surah(15, "الحجر", "Al-Hijr", 99, "مكية"),
        Surah(16, "النحل", "An-Nahl", 128, "مكية"),
        Surah(17, "الإسراء", "Al-Isra", 111, "مكية"),
        Surah(18, "الكهف", "Al-Kahf", 110, "مكية"),
        Surah(19, "مريم", "Maryam", 98, "مكية"),
        Surah(20, "طه", "Taha", 135, "مكية"),
        Surah(21, "الأنبياء", "Al-Anbiya", 112, "مكية"),
        Surah(22, "الحج", "Al-Hajj", 78, "مدنية"),
        Surah(23, "المؤمنون", "Al-Mu'minun", 118, "مكية"),
        Surah(24, "النور", "An-Nur", 64, "مدنية"),
        Surah(25, "الفرقان", "Al-Furqan", 77, "مكية"),
        Surah(26, "الشعراء", "Ash-Shu'ara", 227, "مكية"),
        Surah(27, "النمل", "An-Naml", 93, "مكية"),
        Surah(28, "القصص", "Al-Qasas", 88, "مكية"),
        Surah(29, "العنكبوت", "Al-'Ankabut", 69, "مكية"),
        Surah(30, "الروم", "Ar-Rum", 60, "مكية"),
        Surah(31, "لقمان", "Luqman", 34, "مكية"),
        Surah(32, "السجدة", "As-Sajdah", 30, "مكية"),
        Surah(33, "الأحزاب", "Al-Ahzab", 73, "مدنية"),
        Surah(34, "سبأ", "Saba", 54, "مكية"),
        Surah(35, "فاطر", "Fatir", 45, "مكية"),
        Surah(36, "يس", "Ya-Sin", 83, "مكية"),
        Surah(37, "الصافات", "As-Saffat", 182, "مكية"),
        Surah(38, "ص", "Sad", 88, "مكية"),
        Surah(39, "الزمر", "Az-Zumar", 75, "مكية"),
        Surah(40, "غافر", "Ghafir", 85, "مكية"),
        Surah(41, "فصلت", "Fussilat", 54, "مكية"),
        Surah(42, "الشورى", "Ash-Shura", 53, "مكية"),
        Surah(43, "الزخرف", "Az-Zukhruf", 89, "مكية"),
        Surah(44, "الدخان", "Ad-Dukhan", 59, "مكية"),
        Surah(45, "الجاثية", "Al-Jathiyah", 37, "مكية"),
        Surah(46, "الأحقاف", "Al-Ahqaf", 35, "مكية"),
        Surah(47, "محمد", "Muhammad", 38, "مدنية"),
        Surah(48, "الفتح", "Al-Fath", 29, "مدنية"),
        Surah(49, "الحجرات", "Al-Hujurat", 18, "مدنية"),
        Surah(50, "ق", "Qaf", 45, "مكية"),
        Surah(51, "الذاريات", "Adh-Dhariyat", 60, "مكية"),
        Surah(52, "الطور", "At-Tur", 49, "مكية"),
        Surah(53, "النجم", "An-Najm", 62, "مكية"),
        Surah(54, "القمر", "Al-Qamar", 55, "مكية"),
        Surah(55, "الرحمن", "Ar-Rahman", 78, "مدنية"),
        Surah(56, "الواقعة", "Al-Waqi'ah", 96, "مكية"),
        Surah(57, "الحديد", "Al-Hadid", 29, "مدنية"),
        Surah(58, "المجادلة", "Al-Mujadila", 22, "مدنية"),
        Surah(59, "الحشر", "Al-Hashr", 24, "مدنية"),
        Surah(60, "الممتحنة", "Al-Mumtahanah", 13, "مدنية"),
        Surah(61, "الصف", "As-Saff", 14, "مدنية"),
        Surah(62, "الجمعة", "Al-Jumu'ah", 11, "مدنية"),
        Surah(63, "المنافقون", "Al-Munafiqun", 11, "مدنية"),
        Surah(64, "التغابن", "At-Taghabun", 18, "مدنية"),
        Surah(65, "الطلاق", "At-Talaq", 12, "مدنية"),
        Surah(66, "التحريم", "At-Tahrim", 12, "مدنية"),
        Surah(67, "الملك", "Al-Mulk", 30, "مكية"),
        Surah(68, "القلم", "Al-Qalam", 52, "مكية"),
        Surah(69, "الحاقة", "Al-Haqqah", 52, "مكية"),
        Surah(70, "المعارج", "Al-Ma'arij", 44, "مكية"),
        Surah(71, "نوح", "Nuh", 28, "مكية"),
        Surah(72, "الجن", "Al-Jinn", 28, "مكية"),
        Surah(73, "المزمل", "Al-Muzzammil", 20, "مكية"),
        Surah(74, "المدثر", "Al-Muddaththir", 56, "مكية"),
        Surah(75, "القيامة", "Al-Qiyamah", 40, "مكية"),
        Surah(76, "الإنسان", "Al-Insan", 31, "مدنية"),
        Surah(77, "المرسلات", "Al-Mursalat", 50, "مكية"),
        Surah(78, "النبأ", "An-Naba", 40, "مكية"),
        Surah(79, "النازعات", "An-Nazi'at", 46, "مكية"),
        Surah(80, "عبس", "'Abasa", 42, "مكية"),
        Surah(81, "التكوير", "At-Takwir", 29, "مكية"),
        Surah(82, "الانفطار", "Al-Infitar", 19, "مكية"),
        Surah(83, "المطففين", "Al-Mutaffifin", 36, "مكية"),
        Surah(84, "الانشقاق", "Al-Inshiqaq", 25, "مكية"),
        Surah(85, "البروج", "Al-Buruj", 22, "مكية"),
        Surah(86, "الطارق", "At-Tariq", 17, "مكية"),
        Surah(87, "الأعلى", "Al-A'la", 19, "مكية"),
        Surah(88, "الغاشية", "Al-Ghashiyah", 26, "مكية"),
        Surah(89, "الفجر", "Al-Fajr", 30, "مكية"),
        Surah(90, "البلد", "Al-Balad", 20, "مكية"),
        Surah(91, "الشمس", "Ash-Shams", 15, "مكية"),
        Surah(92, "الليل", "Al-Layl", 21, "مكية"),
        Surah(93, "الضحى", "Ad-Duha", 11, "مكية"),
        Surah(94, "الشرح", "Ash-Sharh", 8, "مكية"),
        Surah(95, "التين", "At-Tin", 8, "مكية"),
        Surah(96, "العلق", "Al-'Alaq", 19, "مكية"),
        Surah(97, "القدر", "Al-Qadr", 5, "مكية"),
        Surah(98, "البينة", "Al-Bayyinah", 8, "مدنية"),
        Surah(99, "الزلزلة", "Az-Zalzalah", 8, "مدنية"),
        Surah(100, "العاديات", "Al-'Adiyat", 11, "مكية"),
        Surah(101, "القارعة", "Al-Qari'ah", 11, "مكية"),
        Surah(102, "التكاثر", "At-Takathur", 8, "مكية"),
        Surah(103, "العصر", "Al-'Asr", 3, "مكية"),
        Surah(104, "الهمزة", "Al-Humazah", 9, "مكية"),
        Surah(105, "الفيل", "Al-Fil", 5, "مكية"),
        Surah(106, "قريش", "Quraysh", 4, "مكية"),
        Surah(107, "الماعون", "Al-Ma'un", 7, "مكية"),
        Surah(108, "الكوثر", "Al-Kawthar", 3, "مكية"),
        Surah(109, "الكافرون", "Al-Kafirun", 6, "مكية"),
        Surah(110, "النصر", "An-Nasr", 3, "مدنية"),
        Surah(111, "المسد", "Al-Masad", 5, "مكية"),
        Surah(112, "الإخلاص", "Al-Ikhlas", 4, "مكية"),
        Surah(113, "الفلق", "Al-Falaq", 5, "مكية"),
        Surah(114, "الناس", "An-Nas", 6, "مكية")
    )

    // In-memory cache for fetched surahs
    private val surahVersesCache = mutableMapOf<Int, List<Ayah>>()

    // Pre-loaded popular verses for offline immediate instant rendering
    private val localVerses: Map<Int, List<Ayah>> = mapOf(
        1 to listOf(
            Ayah(1, "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", "In the name of Allah, the Entirely Merciful, the Especially Merciful.", "أبدأ باسم الله مستعيناً به"),
            Ayah(2, "الْحَمْدُ لِلَّهِ رَبِّ الْعَالَمِينَ", "[All] praise is [due] to Allah, Lord of the worlds -", "الثناء والشكر لله وحده مالك كل شيء"),
            Ayah(3, "الرَّحْمَٰنِ الرَّحِيمِ", "The Entirely Merciful, the Especially Merciful,", "ذو الرحمة الواسعة التي وسعت كل شيء"),
            Ayah(4, "مَالِكِ يَوْمِ الدِّينِ", "Sovereign of the Day of Recompense.", "المالك المتصرف في يوم الحساب والجزاء"),
            Ayah(5, "إِيَّاكَ نَعْبُدُ وَإِيَّاكَ نَسْتَعِينُ", "It is You we worship and You we ask for help.", "نخصك وحدك بالعبادة ونطلب منك وحدك العون"),
            Ayah(6, "اهْدِنَا الصِّرَاطَ الْمُسْتَقِيمَ", "Guide us to the straight path -", "وفقنا وسددنا إلى الطريق المستقيم"),
            Ayah(7, "صِرَاطَ الَّذِينَ أَنْعَمْتَ عَلَيْهِمْ غَيْرِ الْمَغْضُوبِ عَلَيْهِمْ وَلَا الضَّالِّينَ", "The path of those upon whom You have bestowed favor, not of those who have evoked [Your] anger or of those who are astray.", "طريق الأنبياء والصديقين لا طريق المغضوب عليهم ولا الضالين")
        ),
        2 to listOf(
            Ayah(255, "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ ۚ لَا تَأْخُذُهُ سِنَةٌ وَلَا نَوْمٌ ۚ لَّهُ مَا فِي السَّمَاوَاتِ وَمَا فِي الْأَرْضِ ۗ مَن ذَا الَّذِي يَشْفَعُ عِندَهُ إِلَّا بِإِذْنِهِ ۚ يَعْلَمُ مَا بَيْنَ أَيْدِيهِمْ وَمَا خَلْفَهُمْ ۖ وَلَا يُحِيطُونَ بِشَيْءٍ مِّنْ عِلْمِهِ إِلَّا بِمَا شَاءَ ۚ وَسِعَ كُرْسِيُّهُ السَّمَاوَاتِ وَالْأَرْضَ ۖ وَلَا يَئُودُهُ حِفْظُهُمَا ۚ وَهُوَ الْعَلِيُّ الْعَظِيمُ", "Allah - there is no deity except Him, the Ever-Living, the Sustainer of all existence. Neither drowsiness overtakes Him nor sleep.", "آية الكرسي: أعظم آية في كتاب الله تبارك وتعالى"),
            Ayah(285, "آمَنَ الرَّسُولُ بِمَا أُنزِلَ إِلَيْهِ مِن رَّبِّهِ وَالْمُؤْمِنُونَ ۚ كُلٌّ آمَنَ بِاللَّهِ وَمَلَائِكَتِهِ وَكُتُبِهِ وَرُسُلِهِ لَا نُفَرِّقُ بَيْنَ أَحَدٍ مِّن رُّسُلِهِ ۚ وَقَالُوا سَمِعْنَا وَأَطَعْنَا ۖ غُفْرَانَكَ رَبَّنَا وَإِلَيْكَ الْمَصِيرُ", "The Messenger has believed in what was revealed to him from his Lord, and [so have] the believers.", "خواتيم سورة البقرة: من قرأهما في ليلة كفتاه"),
            Ayah(286, "لَا يُكَلِّفُ اللَّهُ نَفْسًا إِلَّا وُسْعَهَا ۚ لَهَا مَا كَسَبَتْ وَعَلَيْهَا مَا اكْتَسَبَتْ ۗ رَبَّنَا لَا تُؤَاخِذْنَا إِن نَّسِينَا أَوْ أَخْطَأْنَا ۚ رَبَّنَا وَلَا تَحْمِلْ عَلَيْنَا إِصْرًا كَمَا حَمَلْتَهُ عَلَى الَّذِينَ مِن قَبْلِنَا ۚ رَبَّنَا وَلَا تُحَمِّلْنَا مَا لَا طَاقَةَ لَنَا بِهِ ۖ وَاعْفُ عَنَّا وَاغْفِرْ لَنَا وَارْحَمْنَا ۚ أَنتَ مَوْلَانَا فَانصُرْنَا عَلَى الْقَوْمِ الْكَافِرِينَ", "Allah does not charge a soul except [with that within] its capacity. It will have [the consequence of] what [good] it has gained.", "دعاء المؤمنين المستجاب بالرحمة والمغفرة")
        ),
        55 to listOf(
            Ayah(1, "الرَّحْمَٰنُ", "The Entirely Merciful", "الله الرحمن ذو الرحمة الشاملة"),
            Ayah(2, "عَلَّمَ الْقُرْآنَ", "Taught the Quran,", "علم نبيه والناس هذا القرآن العظيم"),
            Ayah(3, "خَلَقَ الْإِنسَانَ", "Created man,", "أوجد الإنسان في أحسن تقويم"),
            Ayah(4, "عَلَّمَهُ الْبَيَانَ", "[And] taught him speech.", "علمه الإفصاح والتعبير والنطق"),
            Ayah(5, "الشَّمْسُ وَالْقَمَرُ بِحُسْبَانٍ", "The sun and the moon [move] by precise calculation,", "يجريان بحساب مقدر متقن"),
            Ayah(6, "وَالنَّجْمُ وَالشَّجَرُ يَسْجُدَانِ", "And the stars and trees prostrate,", "تخضع لله وتنقاد لأمره"),
            Ayah(7, "وَالسَّمَاءَ رَفَعَهَا وَوَضَعَ الْمِيزَانَ", "And the heaven He raised and imposed the balance", "رفع السماء عالية وأمر بالعدل"),
            Ayah(8, "أَلَّا تَطْغَوْا فِي الْمِيزَانِ", "That you not transgress within the balance.", "لئلا تعتدوا في الوزن والمكيال")
        ),
        67 to listOf(
            Ayah(1, "تَبَارَكَ الَّذِي بِيَدِهِ الْمُلْكُ وَهُوَ عَلَىٰ كُلِّ شَيْءٍ قَدِيرٌ", "Blessed is He in whose hand is dominion, and He is over all things competent -", "سورة الملك المنجية من عذاب القبر"),
            Ayah(2, "الَّذِي خَلَقَ الْمَوْتَ وَالْحَيَاةَ لِيَبْلُوَكُمْ أَيُّكُمْ أَحْسَنُ عَمَلًا ۚ وَهُوَ الْعَزِيزُ الْغَفُورُ", "[He] who created death and life to test you [as to] which of you is best in deed - and He is the Exalted in Might, the Forgiving -", "ابتلاء واختبار للعباد ليتبين المحسن"),
            Ayah(3, "الَّذِي خَلَقَ سَبْعَ سَمَاوَاتٍ طِبَاقًا ۖ مَّا تَرَىٰ فِي خَلْقِ الرَّحْمَٰنِ مِن تَفَاوُتٍ ۖ فَارْجِعِ الْبَصَرَ هَلْ تَرَىٰ مِن فُطُورٍ", "[And] who created seven heavens in layers. You do not see in the creation of the Most Merciful any inconsistency.", "عظمة خلق الله في السموات بلا خلل")
        ),
        93 to listOf(
            Ayah(1, "وَالضُّحَىٰ", "By the morning brightness", "أقسم الله بوقت ارتفاع الشمس بعد شروقها"),
            Ayah(2, "وَاللَّيْلِ إِذَا سَجَىٰ", "And [by] the night when it covers with darkness,", "أقسم بالليل إذا سكن واشتد ظلامه"),
            Ayah(3, "مَا وَدَّعَكَ رَبُّكَ وَمَا قَلَىٰ", "Your Lord has not taken leave of you, [O Muhammad], nor has He detested [you].", "ما تركك ربك يا محمد وما أبغضك"),
            Ayah(4, "وَلَلْآخِرَةُ خَيْرٌ لَّكَ مِنَ الْأُولَىٰ", "And the Hereafter is better for you than the first [life].", "والدار الآخرة خير لك من الدنيا"),
            Ayah(5, "وَلَسَوْفَ يُعْطِيكَ رَبُّكَ فَتَرْضَىٰ", "And your Lord is going to give you, and you will be satisfied.", "سيعطيك ربك من الخير والكرامة حتى ترضى")
        ),
        94 to listOf(
            Ayah(1, "أَلَمْ نَشْرَحْ لَكَ صَدْرَكَ", "Did We not expand for you, [O Muhammad], your breast?", "ألم نفتح لك صدرك بالهدى والنبوة"),
            Ayah(2, "وَوَضَعْنَا عَنكَ وِزْرَكَ", "And We removed from you your burden", "وحططنا عنك حملك الثقيل"),
            Ayah(3, "الَّذِي أَنقَضَ ظَهْرَكَ", "Which had weighed upon your back", "الذي أثقل ظهرك"),
            Ayah(4, "وَرَفَعْنَا لَكَ ذِكْرَكَ", "And raised high for you your repute.", "وأعلينا منزلتك وذكرك في العالمين"),
            Ayah(5, "فَإِنَّ مَعَ الْعُسْرِ يُسْرًا", "For indeed, with hardship [will be] ease.", "إن مع الضيق والشدة فرجاً وسهولة"),
            Ayah(6, "إِنَّ مَعَ الْعُسْرِ يُسْرًا", "Indeed, with hardship [will be] ease.", "تأكيد بأن بعد كل عسر يسرين"),
            Ayah(7, "فَإِذَا فَرَغْتَ فَانصَبْ", "So when you have finished [your duties], labor hard [in worship].", "فإذا فرغت من أعمالك فانصب في عبادة ربك"),
            Ayah(8, "وَإِلَىٰ رَبِّكَ فَارْغَب", "And to your Lord direct [your] longing.", "واجعل رغبتك وطمعك في رضى ربك وحده")
        ),
        108 to listOf(
            Ayah(1, "إِنَّا أَعْطَيْنَاكَ الْكَوْثَرَ", "Indeed, We have granted you, [O Muhammad], al-Kawthar.", "إنا أعطيناك الخير الكثير ومنه نهر الكوثر"),
            Ayah(2, "فَصَلِّ لِرَبِّكَ وَانْحَرْ", "So pray to your Lord and sacrifice [to Him alone].", "فأخلص لربك صلاتك وذبحك"),
            Ayah(3, "إِنَّ شَانِئَكَ هُوَ الْأَبْتَرُ", "Indeed, your enemy is the one cut off.", "إن مبغضك هو المنقطع ذكره من كل خير")
        ),
        112 to listOf(
            Ayah(1, "قُلْ هُوَ اللَّهُ أَحَدٌ", "Say, 'He is Allah, [who is] One,", "قل يا محمد: هو الله المنفرد بالألوهية"),
            Ayah(2, "اللَّهُ الصَّمَدُ", "Allah, the Eternal Refuge.", "الذي يقصده جميع خلقه في حوائجهم"),
            Ayah(3, "لَمْ يَلِدْ وَلَمْ يُولَدْ", "He neither begets nor is born,", "ليس له ولد ولم يكن له والد"),
            Ayah(4, "وَلَمْ يَكُن لَّهُ كُفُوًا أَحَدٌ", "Nor is there to Him any equivalent.'", "وليس له مثيل ولا شبيه سبحانه")
        ),
        113 to listOf(
            Ayah(1, "قُلْ أَعُوذُ بِرَبِّ الْفَلَقِ", "Say, 'I seek refuge in the Lord of daybreak", "قل: أعتصم وأتحصن برب الصبح"),
            Ayah(2, "مِن شَرِّ مَا خَلَقَ", "From the evil of that which He created", "من شر جميع مخلوقاته"),
            Ayah(3, "وَمِن شَرِّ غَاسِقٍ إِذَا وَقَبَ", "And from the evil of darkness when it settles", "ومن شر الليل إذا أقبل بظلامه"),
            Ayah(4, "وَمِن شَرِّ النَّفَّاثَاتِ فِي الْعُقَدِ", "And from the evil of the blowers in knots", "ومن شر الساحرات اللاتي يعقدن وينفثن"),
            Ayah(5, "وَمِن شَرِّ حَاسِدٍ إِذَا حَسَدَ", "And from the evil of an envier when he envies.'", "ومن شر كل حاسد إذا تمنى زوال النعمة")
        ),
        114 to listOf(
            Ayah(1, "قُلْ أَعُوذُ بِرَبِّ النَّاسِ", "Say, 'I seek refuge in the Lord of mankind,", "قل: أعتصم برب الناس وخالقهم"),
            Ayah(2, "مَلِكِ النَّاسِ", "The Sovereign of mankind,", "مالك جميع شؤونهم وتدبيرهم"),
            Ayah(3, "إِلَٰهِ النَّاسِ", "The God of mankind,", "معبودهم الحق الذي لا معبود سواه"),
            Ayah(4, "مِن شَرِّ الْوَسْوَاسِ الْخَنَّاسِ", "From the evil of the retreating whisperer -", "من شر الشيطان المتربص بالقلوب"),
            Ayah(5, "الَّذِي يُوَسْوِسُ فِي صُدُورِ النَّاسِ", "Who whispers [evil] into the breasts of mankind -", "الذي يبث الشبهات والشهوات"),
            Ayah(6, "مِنَ الْجِنَّةِ وَالنَّاسِ", "From among the jinn and mankind.'", "سواء كان من شياطين الإنس أو الجن")
        )
    )

    suspend fun getSurahVerses(surahNumber: Int): List<Ayah> = withContext(Dispatchers.IO) {
        // Check cache first
        surahVersesCache[surahNumber]?.let { return@withContext it }

        // Check local bundled
        localVerses[surahNumber]?.let {
            surahVersesCache[surahNumber] = it
            return@withContext it
        }

        // Fetch from Quran Cloud API
        try {
            val url = URL("https://api.alquran.cloud/v1/surah/$surahNumber/editions/quran-uthmani,en.sahih")
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 4000
            conn.readTimeout = 4000
            conn.requestMethod = "GET"

            if (conn.responseCode == 200) {
                val jsonText = conn.inputStream.bufferedReader().use { it.readText() }
                val root = JSONObject(jsonText)
                val dataArr = root.getJSONArray("data")
                val arEdition = dataArr.getJSONObject(0).getJSONArray("ayahs")
                val enEdition = if (dataArr.length() > 1) dataArr.getJSONObject(1).getJSONArray("ayahs") else null

                val ayahsList = mutableListOf<Ayah>()
                for (i in 0 until arEdition.length()) {
                    val arObj = arEdition.getJSONObject(i)
                    val num = arObj.getInt("numberInSurah")
                    var arText = arObj.getString("text")
                    // If surah is not Fatihah and not Tawbah, trim the leading Bismillah from verse 1 if present
                    if (surahNumber != 1 && surahNumber != 9 && num == 1 && arText.startsWith("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ ")) {
                        arText = arText.removePrefix("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ ").trim()
                    }
                    val enText = if (enEdition != null && i < enEdition.length()) {
                        enEdition.getJSONObject(i).getString("text")
                    } else ""

                    ayahsList.add(Ayah(numberInSurah = num, textArabic = arText, textEnglish = enText))
                }

                if (ayahsList.isNotEmpty()) {
                    surahVersesCache[surahNumber] = ayahsList
                    return@withContext ayahsList
                }
            }
        } catch (e: Exception) {
            // Fallback: generate placeholder verses if offline and not in local cache
        }

        // Fallback for offline mode when not in local dictionary
        val surahInfo = allSurahs.firstOrNull { it.number == surahNumber } ?: Surah(surahNumber, "سورة $surahNumber", "Surah $surahNumber", 10, "مكية")
        val generated = (1..surahInfo.versesCount).map { num ->
            Ayah(
                numberInSurah = num,
                textArabic = "﴿ سورة ${surahInfo.nameArabic} - الآية $num ﴾",
                textEnglish = "Surah ${surahInfo.nameEnglish} - Verse $num"
            )
        }
        surahVersesCache[surahNumber] = generated
        generated
    }

    suspend fun getVersesRange(surahNumber: Int, fromAyah: Int, toAyah: Int): List<Ayah> {
        val all = getSurahVerses(surahNumber)
        val validFrom = fromAyah.coerceIn(1, all.size)
        val validTo = toAyah.coerceIn(validFrom, all.size)
        return all.filter { it.numberInSurah in validFrom..validTo }
    }
}
