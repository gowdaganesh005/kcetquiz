package com.example.quizapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors

@Database(entities = [Question::class, Option::class, UserAnswer::class, Score::class, User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao
    abstract fun optionDao(): OptionDao
    abstract fun userAnswerDao(): UserAnswerDao
    abstract fun scoreDao(): ScoreDao
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "quiz-db"
                ).addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        Executors.newSingleThreadExecutor().execute {
                            val database = getInstance(context)
                            insertInitialData(database)
                        }
                    }
                }).build()
                INSTANCE = instance
                instance
            }
        }

        private fun insertInitialData(database: AppDatabase) {
    runBlocking {
        val questions = listOf(
            // Physics Questions (IDs 1-10)
            Question(1, "1) What is the SI unit of force?", "Physics"),
            Question(2, "2) How many significant figures are there in 0.0023080?", "Physics"),
            Question(3, "3) What is the dimensional formula of momentum?", "Physics"),
            Question(4, "4) A body is thrown vertically upwards. At the highest point, its velocity is?", "Physics"),
            Question(5, "5) The work done by a force is zero when the angle between force and displacement is?", "Physics"),
            Question(6, "6) The electric field due to a point charge Q at a distance r is?", "Physics"),
            Question(7, "7) In a series LCR circuit at resonance, the current is?", "Physics"),
            Question(8, "8) The SI unit of magnetic flux is?", "Physics"),
            Question(9, "9) The process in which a nucleus emits an alpha particle is called?", "Physics"),
            Question(10, "10) The majority charge carriers in an n-type semiconductor are?", "Physics"),

            // Chemistry Questions (IDs 11-20)
            Question(11, "1) Which of the following has the highest electronegativity?", "Chemistry"),
            Question(12, "2) The IUPAC name of CH3-CH(OH)-CH3 is?", "Chemistry"),
            Question(13, "3) The molecular formula of benzene is?", "Chemistry"),
            Question(14, "4) The number of moles in 22 g of CO2 is?", "Chemistry"),
            Question(15, "5) Which of the following is an intensive property?", "Chemistry"),
            Question(16, "6) Which of the following is a colligative property?", "Chemistry"),
            Question(17, "7) The rate constant for a first-order reaction is 0.0693 min^-1. The half-life is?", "Chemistry"),
            Question(18, "8) The coordination number of Na+ in NaCl crystal is?", "Chemistry"),
            Question(19, "9) Which of the following is not a method of purification of colloids?", "Chemistry"),
            Question(20, "10) The functional group in alcohols is?", "Chemistry"),

            // Mathematics Questions (IDs 21-30)
            Question(21, "1) The number of elements in the power set of a set with 3 elements is?", "Mathematics"),
            Question(22, "2) The value of sin(π/3) is?", "Mathematics"),
            Question(23, "3) The solution of the inequality 2x + 3 > 7 is?", "Mathematics"),
            Question(24, "4) The nth term of an AP is given by a_n = 3 + 2n. The common difference is?", "Mathematics"),
            Question(25, "5) The distance between the points (1,2) and (4,6) is?", "Mathematics"),
            Question(26, "6) The determinant of the matrix [1 2; 3 4] is?", "Mathematics"),
            Question(27, "7) The derivative of sin(x) with respect to x is?", "Mathematics"),
            Question(28, "8) The integral of 1/x dx is?", "Mathematics"),
            Question(29, "9) The probability of getting a head in a fair coin toss is?", "Mathematics"),
            Question(30, "10) The number of ways to arrange 3 distinct books on a shelf is?", "Mathematics"),

            // Biology Questions (IDs 31-40)
            Question(31, "1) The basic unit of classification is?", "Biology"),
            Question(32, "2) Which of the following is a monocotyledon?", "Biology"),
            Question(33, "3) The process by which plants lose water through leaves is?", "Biology"),
            Question(34, "4) The largest phylum in the animal kingdom is?", "Biology"),
            Question(35, "5) The tissue responsible for transport of water in plants is?", "Biology"),
            Question(36, "6) The site of fertilization in humans is?", "Biology"),
            Question(37, "7) Which hormone is responsible for ovulation?", "Biology"),
            Question(38, "8) The process of copying DNA is called?", "Biology"),
            Question(39, "9) The Hardy-Weinberg principle is related to?", "Biology"),
            Question(40, "10) The greenhouse gas that is exclusively anthropogenic is?", "Biology"),

            // Kannada Questions (IDs 41-50)
            Question(41, "೧) ಈ ಕೆಳಗಿನವುಗಳಲ್ಲಿ ಯಾವುದು ನಾಮಪದ?", "Kannada"),
            Question(42, "೨) ಕನ್ನಡ ವರ್ಣಮಾಲೆಯಲ್ಲಿ ಎಷ್ಟು ಸ್ವರಗಳಿವೆ?", "Kannada"),
            Question(43, "೩) ಈ ಕೆಳಗಿನವುಗಳಲ್ಲಿ ಯಾವುದು ಸರ್ವನಾಮ?", "Kannada"),
            Question(44, "೪) ಈ ಕೆಳಗಿನವುಗಳಲ್ಲಿ ಯಾವುದು ಕ್ರಿಯಾಪದ?", "Kannada"),
            Question(45, "೫) ಕನ್ನಡದಲ್ಲಿ ಮೊದಲ ಅಕ್ಷರ ಯಾವುದು?", "Kannada"),
            Question(46, "೬) \"ಪುಸ್ತಕ\" ಎಂಬ ಪದದ ಲಿಂಗ ಯಾವುದು?", "Kannada"),
            Question(47, "೭) \"ಅವನು\" ಎಂಬ ಪದದ ಬಹುವಚನ ರೂಪ ಯಾವುದು?", "Kannada"),
            Question(48, "೮) ಈ ಕೆಳಗಿನವುಗಳಲ್ಲಿ ಯಾವುದು ವಿಶೇಷಣ?", "Kannada"),
            Question(49, "೯) \"ನಾನು ಹೋಗುತ್ತೇನೆ\" ಎಂಬ ವಾಕ್ಯದಲ್ಲಿ \"ಹೋಗುತ್ತೇನೆ\" ಯಾವ ರೂಪದಲ್ಲಿದೆ?", "Kannada"),
            Question(50, "೧೦) ಕನ್ನಡದಲ್ಲಿ ಎಷ್ಟು ವಿಭಕ್ತಿಗಳಿವೆ?", "Kannada")
        )

        database.questionDao().insertAll(questions)

        val options = listOf(
            // Physics Question 1 Options
            Option(1, 1, "Joule", false),
            Option(2, 1, "Newton", true),
            Option(3, 1, "Watt", false),
            Option(4, 1, "Pascal", false),
            // Physics Question 2 Options
            Option(5, 2, "7", false),
            Option(6, 2, "6", false),
            Option(7, 2, "5", true),
            Option(8, 2, "4", false),
            // Physics Question 3 Options
            Option(9, 3, "[M L T^-1]", true),
            Option(10, 3, "[M L T^-2]", false),
            Option(11, 3, "[M L^2 T^-2]", false),
            Option(12, 3, "[M L^2 T^-1]", false),
            // Physics Question 4 Options
            Option(13, 4, "Maximum", false),
            Option(14, 4, "Minimum but not zero", false),
            Option(15, 4, "Same as initial velocity", false),
            Option(16, 4, "Zero", true),
            // Physics Question 5 Options
            Option(17, 5, "180°", false),
            Option(18, 5, "0°", false),
            Option(19, 5, "45°", false),
            Option(20, 5, "90°", true),
            // Physics Question 6 Options
            Option(21, 6, "kQ/r", false),
            Option(22, 6, "Q/(4πε0 r)", false),
            Option(23, 6, "kQ/r^2", true),
            Option(24, 6, "Q/(4πε0 r^2)", false),
            // Physics Question 7 Options
            Option(25, 7, "Zero", false),
            Option(26, 7, "Minimum", false),
            Option(27, 7, "Maximum", true),
            Option(28, 7, "Infinite", false),
            // Physics Question 8 Options
            Option(29, 8, "Tesla", false),
            Option(30, 8, "Henry", false),
            Option(31, 8, "Gauss", false),
            Option(32, 8, "Weber", true),
            // Physics Question 9 Options
            Option(33, 9, "Beta decay", false),
            Option(34, 9, "Alpha decay", true),
            Option(35, 9, "Gamma decay", false),
            Option(36, 9, "Fission", false),
            // Physics Question 10 Options
            Option(37, 10, "Holes", false),
            Option(38, 10, "Both", false),
            Option(39, 10, "None", false),
            Option(40, 10, "Electrons", true),

            // Chemistry Question 11 Options
            Option(41, 11, "Fluorine", true),
            Option(42, 11, "Chlorine", false),
            Option(43, 11, "Oxygen", false),
            Option(44, 11, "Nitrogen", false),
            // Chemistry Question 12 Options
            Option(45, 12, "Propane-2-ol", false),
            Option(46, 12, "Isopropyl alcohol", false),
            Option(47, 12, "Propan-2-ol", true),
            Option(48, 12, "2-Propanol", false),
            // Chemistry Question 13 Options
            Option(49, 13, "C6H12", false),
            Option(50, 13, "C6H14", false),
            Option(51, 13, "C6H10", false),
            Option(52, 13, "C6H6", true),
            // Chemistry Question 14 Options
            Option(53, 14, "0.5", true),
            Option(54, 14, "1", false),
            Option(55, 14, "2", false),
            Option(56, 14, "0.25", false),
            // Chemistry Question 15 Options
            Option(57, 15, "Mass", false),
            Option(58, 15, "Density", true),
            Option(59, 15, "Volume", false),
            Option(60, 15, "Energy", false),
            // Chemistry Question 16 Options
            Option(61, 16, "Viscosity", false),
            Option(62, 16, "Osmotic pressure", true),
            Option(63, 16, "Surface tension", false),
            Option(64, 16, "Refractive index", false),
            // Chemistry Question 17 Options
            Option(65, 17, "40 min", false),
            Option(66, 17, "30 min", false),
            Option(67, 17, "20 min", false),
            Option(68, 17, "10 min", true),
            // Chemistry Question 18 Options
            Option(69, 18, "8", false),
            Option(70, 18, "12", false),
            Option(71, 18, "6", true),
            Option(72, 18, "4", false),
            // Chemistry Question 19 Options
            Option(73, 19, "Distillation", true),
            Option(74, 19, "Dialysis", false),
            Option(75, 19, "Ultrafiltration", false),
            Option(76, 19, "Electrophoresis", false),
            // Chemistry Question 20 Options
            Option(77, 20, "-COOH", false),
            Option(78, 20, "-CHO", false),
            Option(79, 20, "-NH2", false),
            Option(80, 20, "-OH", true),

            // Mathematics Question 21 Options
            Option(81, 21, "8", true),
            Option(82, 21, "6", false),
            Option(83, 21, "4", false),
            Option(84, 21, "9", false),
            // Mathematics Question 22 Options
            Option(85, 22, "1/2", false),
            Option(86, 22, "√3/2", true),
            Option(87, 22, "√2/2", false),
            Option(88, 22, "1", false),
            // Mathematics Question 23 Options
            Option(89, 23, "x < 2", false),
            Option(90, 23, "x < 3", false),
            Option(91, 23, "x > 3", false),
            Option(92, 23, "x > 2", true),
            // Mathematics Question 24 Options
            Option(93, 24, "3", false),
            Option(94, 24, "2", true),
            Option(95, 24, "5", false),
            Option(96, 24, "1", false),
            // Mathematics Question 25 Options
            Option(97, 25, "√5", false),
            Option(98, 25, "3", false),
            Option(99, 25, "5", true),
            Option(100, 25, "√13", false),
            // Mathematics Question 26 Options
            Option(101, 26, "2", false),
            Option(102, 26, "-1", false),
            Option(103, 26, "1", false),
            Option(104, 26, "-2", true),
            // Mathematics Question 27 Options
            Option(105, 27, "-sin(x)", false),
            Option(106, 27, "tan(x)", false),
            Option(107, 27, "cos(x)", true),
            Option(108, 27, "sec(x)", false),
            // Mathematics Question 28 Options
            Option(109, 28, "x^2/2 + C", false),
            Option(110, 28, "ln|x| + C", true),
            Option(111, 28, "e^x + C", false),
            Option(112, 28, "1/x + C", false),
            // Mathematics Question 29 Options
            Option(113, 29, "1/3", false),
            Option(114, 29, "1/4", false),
            Option(115, 29, "1", false),
            Option(116, 29, "1/2", true),
            // Mathematics Question 30 Options
            Option(117, 30, "2", false),
            Option(118, 30, "1", false),
            Option(119, 30, "6", true),
            Option(120, 30, "3", false),

            // Biology Question 31 Options
            Option(121, 31, "Species", true),
            Option(122, 31, "Genus", false),
            Option(123, 31, "Family", false),
            Option(124, 31, "Order", false),
            // Biology Question 32 Options
            Option(125, 32, "Pea", false),
            Option(126, 32, "Wheat", true),
            Option(127, 32, "Gram", false),
            Option(128, 32, "Bean", false),
            // Biology Question 33 Options
            Option(129, 33, "Photosynthesis", false),
            Option(130, 33, "Respiration", false),
            Option(131, 33, "Transpiration", true),
            Option(132, 33, "Fermentation", false),
            // Biology Question 34 Options
            Option(133, 34, "Mollusca", false),
            Option(134, 34, "Arthropoda", true),
            Option(135, 34, "Chordata", false),
            Option(136, 34, "Annelida", false),
            // Biology Question 35 Options
            Option(137, 35, "Phloem", false),
            Option(138, 35, "Xylem", true),
            Option(139, 35, "Parenchyma", false),
            Option(140, 35, "Collenchyma", false),
            // Biology Question 36 Options
            Option(141, 36, "Uterus", false),
            Option(142, 36, "Ovary", false),
            Option(143, 36, "Fallopian tube", true),
            Option(144, 36, "Cervix", false),
            // Biology Question 37 Options
            Option(145, 37, "FSH", false),
            Option(146, 37, "LH", true),
            Option(147, 37, "Estrogen", false),
            Option(148, 37, "Progesterone", false),
            // Biology Question 38 Options
            Option(149, 38, "Transcription", false),
            Option(150, 38, "Translation", false),
            Option(151, 38, "Mutation", false),
            Option(152, 38, "Replication", true),
            // Biology Question 39 Options
            Option(153, 39, "Evolution", false),
            Option(154, 39, "Population genetics", true),
            Option(155, 39, "Ecology", false),
            Option(156, 39, "Taxonomy", false),
            // Biology Question 40 Options
            Option(157, 40, "CO2", false),
            Option(158, 40, "CFCs", true),
            Option(159, 40, "CH4", false),
            Option(160, 40, "N2O", false),

            // Kannada Question 41 Options
            Option(161, 41, "ಮನೆ", true),
            Option(162, 41, "ಹೋಗು", false),
            Option(163, 41, "ಚೆನ್ನಾಗಿ", false),
            Option(164, 41, "ಬೇಗ", false),
            // Kannada Question 42 Options
            Option(165, 42, "14", false),
            Option(166, 42, "15", false),
            Option(167, 42, "13", true),
            Option(168, 42, "16", false),
            // Kannada Question 43 Options
            Option(169, 43, "ಹೋಗು", false),
            Option(170, 43, "ಮನೆ", false),
            Option(171, 43, "ನಾನು", true),
            Option(172, 43, "ಚೆನ್ನಾಗಿ", false),
            // Kannada Question 44 Options
            Option(173, 44, "ಸುಂದರ", false),
            Option(174, 44, "ಮನೆ", false),
            Option(175, 44, "ಓಡು", true),
            Option(176, 44, "ಅವರು", false),
            // Kannada Question 45 Options
            Option(177, 45, "ಆ", false),
            Option(178, 45, "ಇ", false),
            Option(179, 45, "ಈ", false),
            Option(180, 45, "ಅ", true),
            // Kannada Question 46 Options
            Option(181, 46, "ಸ್ತ್ರೀಲಿಂಗ", false),
            Option(182, 46, "ಪುಲ್ಲಿಂಗ", false),
            Option(183, 46, "ನಪುಂಸಕಲಿಂಗ", true),
            Option(184, 46, "ಉಭಯಲಿಂಗ", false),
            // Kannada Question 47 Options
            Option(185, 47, "ಅವನುಗಳು", false),
            Option(186, 47, "ಅವರು", true),
            Option(187, 47, "ಅವಳು", false),
            Option(188, 47, "ಅದು", false),
            // Kannada Question 48 Options
            Option(189, 48, "ಮನೆ", false),
            Option(190, 48, "ಸುಂದರ", true),
            Option(191, 48, "ಹೋಗು", false),
            Option(192, 48, "ಬೇಗ", false),
            // Kannada Question 49 Options
            Option(193, 49, "ವರ್ತಮಾನ ಕಾಲ", true),
            Option(194, 49, "ಭೂತಕಾಲ", false),
            Option(195, 49, "ಭವಿಷ್ಯತ್ ಕಾಲ", false),
            Option(196, 49, "ಆಜ್ಞಾರ್ಥ", false),
            // Kannada Question 50 Options
            Option(197, 50, "8", false),
            Option(198, 50, "9", false),
            Option(199, 50, "10", false),
            Option(200, 50, "7", true)
        )

        database.optionDao().insertAll(options)
    }
}
}
}