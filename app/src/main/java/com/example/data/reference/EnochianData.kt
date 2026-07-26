package com.example.data.reference

data class EnochianCall(
    val id: Int,
    val title: String,
    val subtitle: String,
    val element: String, // Air, Fire, Water, Earth, Spirit, Aethyrs
    val eNochianPhonetic: String,
    val englishTranslation: String,
    val purpose: String,
    val pronunciationGuide: String,
    val frequencyHz: Float
)

data class EnochianLetter(
    val name: String,
    val enochianChar: String,
    val englishChar: Char,
    val gematriaValue: Int,
    val elementalAttribute: String,
    val wheelAngleDegrees: Float
)

data class WatchtowerInfo(
    val name: String,
    val element: String,
    val direction: String,
    val elementalColorHex: String,
    val greatKing: String,
    val seniors: List<String>,
    val gridLetters: List<List<String>>, // 5x6 grid
    val description: String
)

data class AethyrInfo(
    val number: Int,
    val name: String,
    val meaning: String,
    val governors: String,
    val description: String
)

data class EnochianSigilEntry(
    val id: Int,
    val name: String,
    val category: String,
    val traditionalMeaning: String,
    val purpose: String,
    val planet: String,
    val element: String,
    val angelicRuler: String,
    val gemstone: String,
    val intentionPhrase: String,
    val wheelColorHex: String
)

data class PlanetaryCorrespondence(
    val id: Int,
    val planetName: String,
    val symbol: String,
    val dayOfWeek: String,
    val element: String,
    val enochianSenior: String,
    val angelicRuler: String,
    val metal: String,
    val incense: String,
    val gemstone: String,
    val colorHex: String,
    val magicalDomain: String,
    val enochianCallsAssociated: String,
    val sigilPhrase: String
)


object EnochianData {

    val ENNOCHIAN_LETTERS = listOf(
        EnochianLetter("Un", "𐤀", 'A', 6, "Air / Yellow", 0f),
        EnochianLetter("Pa", "𐤁", 'B', 5, "Water / Blue", 17.14f),
        EnochianLetter("Veh", "𐤂", 'C', 300, "Fire / Red", 34.28f),
        EnochianLetter("Ged", "𐤃", 'D', 4, "Earth / Black", 51.42f),
        EnochianLetter("Gal", "𐤄", 'E', 8, "Spirit / White", 68.57f),
        EnochianLetter("Graph", "𐤅", 'F', 3, "Water / Blue", 85.71f),
        EnochianLetter("Tal", "𐤆", 'G', 9, "Earth / Green", 102.85f),
        EnochianLetter("Gon", "𐤈", 'H', 8, "Air / Yellow", 120.0f),
        EnochianLetter("Na", "𐤉", 'I', 60, "Fire / Red", 137.14f),
        EnochianLetter("Ur", "𐤋", 'L', 80, "Water / Blue", 154.28f),
        EnochianLetter("Med", "M", 'M', 90, "Earth / Dark", 171.42f),
        EnochianLetter("Mals", "N", 'N', 40, "Air / Yellow", 188.57f),
        EnochianLetter("Ger", "EF", 'O', 70, "Spirit / Light", 205.71f),
        EnochianLetter("Fam", "P", 'P', 400, "Fire / Flame", 222.85f),
        EnochianLetter("Gisg", "Q", 'Q', 70, "Water / Blue", 240.0f),
        EnochianLetter("Don", "R", 'R', 100, "Air / Gold", 257.14f),
        EnochianLetter("Ceph", "S", 'S', 7, "Earth / Brown", 274.28f),
        EnochianLetter("Tau", "T", 'T', 9, "Fire / Orange", 291.42f),
        EnochianLetter("Vau", "V", 'U', 70, "Water / Blue", 308.57f),
        EnochianLetter("Pal", "X", 'X', 300, "Spirit / Purple", 325.71f),
        EnochianLetter("Zod", "Z", 'Z', 9, "Air / Silver", 342.85f)
    )

    val CALLS = listOf(
        EnochianCall(
            id = 1,
            title = "First Key: Invocation of the Divinity",
            subtitle = "Sovereign Command & Creation",
            element = "Spirit of Spirit",
            eNochianPhonetic = "Iolcamila oh-lahm-ah bah-lah-toh-hah, soh-boh-lahm ah-lah bah-lah-tay-jah dah-rah-pahl kah-rah-lah-soh...",
            englishTranslation = "I reign over you, saith the God of Justice, in power exalted above the firmaments of wrath...",
            purpose = "Establishing divine authority, opening ritual space, and invoking sovereign creation forces.",
            pronunciationGuide = "Vibrate 'IOLCAM' as EEE-OHL-KAHM. Stress each vowel with deep rhythmic breath.",
            frequencyHz = 432.0f
        ),
        EnochianCall(
            id = 2,
            title = "Second Key: Invocation of the Heavenly Hierarchy",
            subtitle = "Wisdom & Cosmic Order",
            element = "Spirit of Fire",
            eNochianPhonetic = "Adagita vau-pa-aa zongom fa-a-ip lap zod-ira-zod com-selh azien bi-en...",
            englishTranslation = "Can the wings of the winds understand your voices of wonder? O ye the second of the First...",
            purpose = "Opening communion with angelic seniors and establishing cosmic order in ceremony.",
            pronunciationGuide = "Vibrate 'ADAGITA' as AH-DAH-GEE-TAH. Extend 'zongom' into a resonant hum.",
            frequencyHz = 528.0f
        ),
        EnochianCall(
            id = 3,
            title = "Third Key: Invocation of Air",
            subtitle = "Watchtower of the East",
            element = "Air",
            eNochianPhonetic = "Micama goho Pe-Iad zod-ir com-selh a-zien bi-en gi-u-ri-s zod-o-ca-ma...",
            englishTranslation = "Behold, saith your God, I am a circle on whose hands stand Twelve Kingdoms...",
            purpose = "Invocations of intellectual clarity, communication, wisdom, and East elemental forces.",
            pronunciationGuide = "Vibrate 'MICAMA' as MEE-KAH-MAH. 'Pe-Iad' as PAY-EE-AHD with clear resonance.",
            frequencyHz = 639.0f
        ),
        EnochianCall(
            id = 4,
            title = "Fourth Key: Invocation of Water",
            subtitle = "Watchtower of the West",
            element = "Water",
            eNochianPhonetic = "Othil lasdi babage od dorpha gohol gizi-yaz zod-ir-a-zod com-selh...",
            englishTranslation = "I have set my feet in the South and have looked about me, saying: Are not the thunders of increase numbered...",
            purpose = "Intuition, emotional purification, fluid transformation, and West elemental forces.",
            pronunciationGuide = "Vibrate 'OTHIL' as OH-THEEL. Breathe steadily in rhythmic cadence.",
            frequencyHz = 741.0f
        ),
        EnochianCall(
            id = 5,
            title = "Fifth Key: Invocation of Earth",
            subtitle = "Watchtower of the North",
            element = "Earth",
            eNochianPhonetic = "Sapah zod-imii du-i-be od noas ta-qani-s zod-a-ca-ma com-selh...",
            englishTranslation = "The mighty sounds have entered in the North, and I have said: It is prepared...",
            purpose = "Manifestation, physical stability, sanctuary protection, and North elemental forces.",
            pronunciationGuide = "Vibrate 'SAPAH' as SAH-PAH with deep pitch grounded in the chest.",
            frequencyHz = 852.0f
        ),
        EnochianCall(
            id = 6,
            title = "Sixth Key: Invocation of Fire",
            subtitle = "Watchtower of the South",
            element = "Fire",
            eNochianPhonetic = "Ga-he sa-div zod-ir-a-zod com-selh azien bi-en gi-u-ri-s zod-o-ca-ma...",
            englishTranslation = "The spirits of the fourth angle are Nine, Mighty in the firmament of waters...",
            purpose = "Willpower, spiritual purification, courage, passion, and South elemental forces.",
            pronunciationGuide = "Vibrate 'GAHE' as GAH-HAY with fiery intent and crisp consonants.",
            frequencyHz = 963.0f
        ),
        EnochianCall(
            id = 7,
            title = "Seventh Key: Invocation of the Tablet of Union",
            subtitle = "The Quintessence / Spirit",
            element = "Spirit",
            eNochianPhonetic = "Ra-as i-sa-lah-ma-ca-oh tah-vah-lah bah-lah-toh-hah oh-dah koh-rah-soh...",
            englishTranslation = "The East is a house of virgins singing praises among the flames of first glory...",
            purpose = "Unifying elemental forces, invoking the spirit element, and binding ritual intent.",
            pronunciationGuide = "Vibrate 'RAAS' as RAH-AHS with full diaphragmatic vocal resonance.",
            frequencyHz = 396.0f
        ),
        EnochianCall(
            id = 8,
            title = "Eighth Key: Invocation of the 1st Aethyr Governors",
            subtitle = "Wisdom of the Elders",
            element = "30 Aethyrs",
            eNochianPhonetic = "Bazmelo i-ta pi-rip-son zod-a-ca-ma com-selh a-zien bi-en...",
            englishTranslation = "The mid-day, the first is as the third floor whose pinnacles are as hyacinth...",
            purpose = "Ascending spiritual consciousness and invoking angelic seniors of governance.",
            pronunciationGuide = "Vibrate 'BAZMELO' as BAHZ-MAY-LOH. Smooth, ascending tone.",
            frequencyHz = 417.0f
        ),
        EnochianCall(
            id = 9,
            title = "Ninth Key: Invocation of Astral Vision",
            subtitle = "Revelation & Prophecy",
            element = "30 Aethyrs",
            eNochianPhonetic = "Micaoli zod-ir-a-zod com-selh azien bi-en gi-u-ri-s zod-o-ca-ma...",
            englishTranslation = "A guard of fire with two-edged swords flaming which have vials eight of wrath...",
            purpose = "Enhancing scrying clarity, opening visionary sight, and astral exploration.",
            pronunciationGuide = "Vibrate 'MICAOLI' as MEE-KAH-OH-LEE with clear high pitch.",
            frequencyHz = 714.0f
        ),
        EnochianCall(
            id = 10,
            title = "Tenth Key: Invocation of Justice & Truth",
            subtitle = "The Unyielding Law",
            element = "30 Aethyrs",
            eNochianPhonetic = "Coraxo chis zod-ir-a-zod com-selh azien bi-en gi-u-ri-s...",
            englishTranslation = "The thunders of judgment and wrath are numbered and harbored in the North...",
            purpose = "Establishing absolute truth, dispelling illusion, and balancing karma.",
            pronunciationGuide = "Vibrate 'CORAXO' as KOH-RAHK-SOH with firm vocal projection.",
            frequencyHz = 888.0f
        ),
        EnochianCall(
            id = 11,
            title = "Eleventh Key: Invocation of Transmutation",
            subtitle = "Alchemical Alchemy",
            element = "30 Aethyrs",
            eNochianPhonetic = "Oxiayal holdo zod-ir-a-zod com-selh azien bi-en gi-u-ri-s...",
            englishTranslation = "The seat of the North is grounded in the eternal fires of renewal...",
            purpose = "Inner transformation, spiritual alchemy, and shedding old mental patterns.",
            pronunciationGuide = "Vibrate 'OXIAYAL' as OHK-SEE-AH-YAHL with swelling resonance.",
            frequencyHz = 528.0f
        ),
        EnochianCall(
            id = 12,
            title = "Twelfth Key: Invocation of Radiant Light",
            subtitle = "Illumination of Sol",
            element = "30 Aethyrs",
            eNochianPhonetic = "Nonci daph zod-ir-a-zod com-selh azien bi-en gi-u-ri-s...",
            englishTranslation = "O ye that range in the South and are the lanterns of sorrow, bind up your girdles...",
            purpose = "Invoking divine brilliance, vitality, confidence, and solar energy.",
            pronunciationGuide = "Vibrate 'NONCI' as NOHN-SEE with warm chest tone.",
            frequencyHz = 639.0f
        ),
        EnochianCall(
            id = 13,
            title = "Thirteenth Key: Invocation of the Deep Mysteries",
            subtitle = "The Abyss & Regeneration",
            element = "30 Aethyrs",
            eNochianPhonetic = "Napeai zod-ir-a-zod com-selh azien bi-en gi-u-ri-s...",
            englishTranslation = "O ye swords of the South, which have 42 eyes to stir up the wrath of the Father...",
            purpose = "Exploring hidden knowledge, shadow integration, and profound spiritual renewal.",
            pronunciationGuide = "Vibrate 'NAPEAI' as NAH-PAY-AH-EE in deep solemn cadence.",
            frequencyHz = 432.0f
        ),
        EnochianCall(
            id = 14,
            title = "Fourteenth Key: Invocation of Equilibrium",
            subtitle = "Harmony of Elements",
            element = "30 Aethyrs",
            eNochianPhonetic = "Noromi bagie zod-ir-a-zod com-selh azien bi-en gi-u-ri-s...",
            englishTranslation = "O ye sons of the mighty, which sit upon the 24 seats, vexing all creatures...",
            purpose = "Restoring balance between microcosm and macrocosm, peace and tranquility.",
            pronunciationGuide = "Vibrate 'NOROMI' as NOH-ROH-MEE with gentle wave-like rhythm.",
            frequencyHz = 440.0f
        ),
        EnochianCall(
            id = 15,
            title = "Fifteenth Key: Invocation of Heavenly Fortress",
            subtitle = "Aegis & Sacred Shield",
            element = "30 Aethyrs",
            eNochianPhonetic = "Ils tabaan zod-ir-a-zod com-selh azien bi-en gi-u-ri-s...",
            englishTranslation = "O thou governor of the first flame, under whose wings are 6732...",
            purpose = "Constructing an impenetrable psychic barrier and invoking divine protection.",
            pronunciationGuide = "Vibrate 'ILS TABAAN' as EELS TAH-BAH-AHN with strong intent.",
            frequencyHz = 963.0f
        ),
        EnochianCall(
            id = 16,
            title = "Sixteenth Key: Invocation of Cosmic Lightning",
            subtitle = "Awakening Force",
            element = "30 Aethyrs",
            eNochianPhonetic = "Ils vivial zod-ir-a-zod com-selh azien bi-en gi-u-ri-s...",
            englishTranslation = "O thou second flame, the house of justice, who hast thy beginning in glory...",
            purpose = "Sudden breakthroughs, energetic vitality, and shattering mental blockages.",
            pronunciationGuide = "Vibrate 'VIVIAL' as VEE-VEE-AHL with energetic emphasis.",
            frequencyHz = 852.0f
        ),
        EnochianCall(
            id = 17,
            title = "Seventeenth Key: Invocation of the Cosmic Loom",
            subtitle = "Weaving Destiny",
            element = "30 Aethyrs",
            eNochianPhonetic = "Ils dial zod-ir-a-zod com-selh azien bi-en gi-u-ri-s...",
            englishTranslation = "O thou third flame! whose wings are thorns to stir up vexation...",
            purpose = "Setting high long-term intentions and aligning personal path with divine purpose.",
            pronunciationGuide = "Vibrate 'DIAL' as DEE-AHL with slow deliberate resonance.",
            frequencyHz = 741.0f
        ),
        EnochianCall(
            id = 18,
            title = "Eighteenth Key: Invocation of Eternal Knowledge",
            subtitle = "The Gnosis",
            element = "30 Aethyrs",
            eNochianPhonetic = "Ils micaoli zod-ir-a-zod com-selh azien bi-en gi-u-ri-s...",
            englishTranslation = "O thou mighty light and burning flame of comfort! that openest the glory of God...",
            purpose = "Unlocking ancestral wisdom, esoteric study comprehension, and divine gnosis.",
            pronunciationGuide = "Vibrate 'MICAOLI' as MEE-KAH-OH-LEE in high resonant key.",
            frequencyHz = 1080.0f
        ),
        EnochianCall(
            id = 19,
            title = "Nineteenth Key: Call of the 30 Aethyrs",
            subtitle = "Universal Ascent through 30 Aethyrs",
            element = "30 Aethyrs",
            eNochianPhonetic = "Madriaax chis oiad saami zod-ir-a-zod com-selh azien bi-en gi-u-ri-s [Insert Name of Aethyr e.g. LIL]...",
            englishTranslation = "O ye heavens which dwell in the First Aethyr, ye are mighty in the parts of the Earth...",
            purpose = "Invocations of any of the 30 Aethyrs (LIL to TEX) by substituting the Aethyr name into the call.",
            pronunciationGuide = "Vibrate 'MADRIAAX' as MAH-DREE-AH-AHKS. Insert chosen Aethyr name with reverence.",
            frequencyHz = 432.0f
        )
    )

    val WATCHTOWERS = listOf(
        WatchtowerInfo(
            name = "Watchtower of the East",
            element = "Air",
            direction = "East",
            elementalColorHex = "#FFE082",
            greatKing = "BATAIVAH",
            seniors = listOf("HABIORO", "AAOXIF", "HTORDA", "AHAOZPI", "HIPOTIS", "AOBZIR"),
            gridLetters = listOf(
                listOf("r", "Z", "i", "l", "f"),
                listOf("a", "f", "A", "l", "o"),
                listOf("a", "r", "p", "a", "C"),
                listOf("O", "x", "i", "a", "y"),
                listOf("S", "a", "a", "z", "i")
            ),
            description = "Associated with intellectual enlightenment, morning light, East wind, and golden yellow ether."
        ),
        WatchtowerInfo(
            name = "Watchtower of the South",
            element = "Fire",
            direction = "South",
            elementalColorHex = "#FF5252",
            greatKing = "EDLPRNAA",
            seniors = listOf("AAETPIO", "ADAOAI", "ANAAEM", "ALAIOD", "AAMSMO", "ACZINOR"),
            gridLetters = listOf(
                listOf("D", "a", "o", "m", "p"),
                listOf("C", "a", "c", "r", "g"),
                listOf("O", "i", "i", "t", "z"),
                listOf("Z", "i", "d", "a", "c"),
                listOf("A", "c", "z", "i", "n")
            ),
            description = "Associated with fiery passion, noon heat, South wind, willpower, and glowing crimson flame."
        ),
        WatchtowerInfo(
            name = "Watchtower of the West",
            element = "Water",
            direction = "West",
            elementalColorHex = "#40C4FF",
            greatKing = "RAAGIOSL",
            seniors = listOf("LSRAHP", "SAAIZX", "SLGAIOL", "SOAIZN", "LIGDIS", "SONIZNT"),
            gridLetters = listOf(
                listOf("S", "a", "a", "i", "z"),
                listOf("M", "p", "h", "a", "r"),
                listOf("G", "a", "i", "o", "l"),
                listOf("L", "i", "g", "d", "i"),
                listOf("O", "b", "m", "a", "c")
            ),
            description = "Associated with fluid emotion, twilight dusk, West wind, scrying water, and deep cobalt ocean."
        ),
        WatchtowerInfo(
            name = "Watchtower of the North",
            element = "Earth",
            direction = "North",
            elementalColorHex = "#69F0AE",
            greatKing = "ICZHIHAL",
            seniors = listOf("LAXDIZA", "LAIDROM", "LZINOPO", "ALIKRA", "AHMLICU", "AICOPO"),
            gridLetters = listOf(
                listOf("b", "o", "A", "p", "a"),
                listOf("O", "p", "n", "a", "m"),
                listOf("I", "a", "m", "d", "a"),
                listOf("S", "c", "i", "o", "p"),
                listOf("A", "l", "i", "k", "r")
            ),
            description = "Associated with physical grounding, midnight depth, North wind, stability, and emerald earth."
        ),
        WatchtowerInfo(
            name = "Tablet of Union",
            element = "Spirit / Quintessence",
            direction = "Center",
            elementalColorHex = "#B388FF",
            greatKing = "EXARP-HCOMA-NANTA-BITOM",
            seniors = listOf("EXARP (Air)", "HCOMA (Water)", "NANTA (Earth)", "BITOM (Fire)"),
            gridLetters = listOf(
                listOf("E", "X", "A", "R", "P"),
                listOf("H", "C", "O", "M", "A"),
                listOf("N", "A", "N", "T", "A"),
                listOf("B", "I", "T", "O", "M")
            ),
            description = "The central binder of the four elements. Governs spirit, quintessence, and divine equilibrium."
        )
    )

    val AETHYRS = listOf(
        AethyrInfo(1, "LIL", "First Aethyr - The Highest Light", "Governed by 3 Seniors", "The realm of pure divine radiance and supreme unity."),
        AethyrInfo(2, "ARN", "Second Aethyr - The Solar Realm", "Governed by 3 Seniors", "The sphere of spiritual illumination and solar glory."),
        AethyrInfo(3, "ZOM", "Third Aethyr - The Fire of Creation", "Governed by 3 Seniors", "The dynamic force of divine desire and creative impulse."),
        AethyrInfo(4, "PAZ", "Fourth Aethyr - The Sanctuary", "Governed by 3 Seniors", "The holy precinct of inner peace and equilibrium."),
        AethyrInfo(5, "LIT", "Fifth Aethyr - The Crystal Sphere", "Governed by 3 Seniors", "Reflective wisdom and clarity of higher mental planes."),
        AethyrInfo(6, "MAZ", "Sixth Aethyr - The Cosmic Temple", "Governed by 3 Seniors", "The celestial architecture of divine law."),
        AethyrInfo(7, "DEO", "Seventh Aethyr - The Gate of Stars", "Governed by 3 Seniors", "The expansive web of cosmic consciousness."),
        AethyrInfo(8, "ZID", "Eighth Aethyr - The Golden Flame", "Governed by 3 Seniors", "Purification and spiritual refinement."),
        AethyrInfo(9, "ZIP", "Ninth Aethyr - The Astral Sea", "Governed by 3 Seniors", "The realm of vision, dream imagery, and scrying."),
        AethyrInfo(10, "ZAX", "Tenth Aethyr - The Great Abyss", "Governed by Choronzon", "The veil between mortal perception and divine truth."),
        AethyrInfo(11, "ICH", "Eleventh Aethyr - The Sacred City", "Governed by 3 Seniors", "The assembly of spiritual masters and guardians."),
        AethyrInfo(12, "LOE", "Twelfth Aethyr - The Rose Garden", "Governed by 3 Seniors", "Ethereal beauty and spiritual ecstasy."),
        AethyrInfo(13, "ZIM", "Thirteenth Aethyr - The Garden of Spices", "Governed by 3 Seniors", "Intuitive knowledge and divine scents."),
        AethyrInfo(14, "UTA", "Fourteenth Aethyr - The City of Pyramids", "Governed by 3 Seniors", "Stability, ancient memory, and enduring wisdom."),
        AethyrInfo(15, "OXO", "Fifteenth Aethyr - The Dance of Stars", "Governed by 3 Seniors", "Dynamic cosmic movement and celestial music."),
        AethyrInfo(16, "LEA", "Sixteenth Aethyr - The River of Life", "Governed by 3 Seniors", "Continuous flow of life force and vital prana."),
        AethyrInfo(17, "TAN", "Seventeenth Aethyr - The Balance", "Governed by 3 Seniors", "The scales of karmic justice and cosmic harmony."),
        AethyrInfo(18, "ZEN", "Eighteenth Aethyr - The Vault of Heaven", "Governed by 3 Seniors", "Overarching cosmic protection and sanctuary."),
        AethyrInfo(19, "POP", "Nineteenth Aethyr - The Flaming Sword", "Governed by 3 Seniors", "Active defense and spiritual authority."),
        AethyrInfo(20, "CHR", "Twentieth Aethyr - The Wheel of Cycles", "Governed by 3 Seniors", "Understanding time, seasons, and natural cycles."),
        AethyrInfo(21, "ASP", "Twenty-First Aethyr - The Foundation", "Governed by 3 Seniors", "Grounded spiritual power in daily practice."),
        AethyrInfo(22, "LIN", "Twenty-Second Aethyr - The Mirror", "Governed by 3 Seniors", "Self-knowledge and reflection of inner intent."),
        AethyrInfo(23, "TOR", "Twenty-Third Aethyr - The Tower of Light", "Governed by 3 Seniors", "Unshakeable fortitude and inspiration."),
        AethyrInfo(24, "NIA", "Twenty-Fourth Aethyr - The Portal", "Governed by 3 Seniors", "Transition between physical and subtle dimensions."),
        AethyrInfo(25, "UTI", "Twenty-Fifth Aethyr - The Sacred Grove", "Governed by 3 Seniors", "Nature spirits and organic harmony."),
        AethyrInfo(26, "DES", "Twenty-Sixth Aethyr - The Dawn", "Governed by 3 Seniors", "New beginnings and hope."),
        AethyrInfo(27, "ZAA", "Twenty-Seventh Aethyr - The Solitary Peak", "Governed by 3 Seniors", "Meditation, solitude, and inner communion."),
        AethyrInfo(28, "BAG", "Twenty-Eighth Aethyr - The Crucible", "Governed by 3 Seniors", "Testing and tempering of ritual resolve."),
        AethyrInfo(29, "RII", "Twenty-Ninth Aethyr - The Outer Threshold", "Governed by 3 Seniors", "Preparation before ascending into higher Aethyrs."),
        AethyrInfo(30, "TEX", "Thirtieth Aethyr - The Foundation of Earth", "Governed by 4 Governors", "The lowest Aethyr closest to the physical plane.")
    )

    val SIGIL_GLOSSARY = listOf(
        EnochianSigilEntry(
            id = 1,
            name = "Sigillum Dei Aemeth",
            category = "Dei Aemeth",
            traditionalMeaning = "The Great Seal of Divine Truth, containing the sacred names of God, the seven planetary angels, and the angelic hierarchy engraved in wax under the Shewstone.",
            purpose = "Ultimate spiritual protection, grounding divine authority during scrying, and insulating the holy altar from hostile astral interference.",
            planet = "Sun & All Planets",
            element = "Quintessence / Spirit",
            angelicRuler = "Metatron & Michael",
            gemstone = "Pure Wax / Clear Quartz",
            intentionPhrase = "DEI AEMETH TRUTH",
            wheelColorHex = "#FFD54F"
        ),
        EnochianSigilEntry(
            id = 2,
            name = "Sigil of Bataivah (East King)",
            category = "Watchtower Kings",
            traditionalMeaning = "The geometric sigil traced from the Holy Name BATAIVAH upon the Air Watchtower Rose Wheel.",
            purpose = "Invocations of intellectual clarity, mental focus, rapid learning, clear speech, and East elemental mastery.",
            planet = "Jupiter in Air",
            element = "Air",
            angelicRuler = "King BATAIVAH & Habioro",
            gemstone = "Yellow Topaz",
            intentionPhrase = "BATAIVAH AIR KING",
            wheelColorHex = "#FFE082"
        ),
        EnochianSigilEntry(
            id = 3,
            name = "Sigil of Edlprnaa (South King)",
            category = "Watchtower Kings",
            traditionalMeaning = "The fiery sigil formed by tracing the Holy Name EDLPRNAA across the Watchtower of Fire.",
            purpose = "Igniting spiritual willpower, courage in adversity, purification of intent, and South elemental power.",
            planet = "Mars in Fire",
            element = "Fire",
            angelicRuler = "King EDLPRNAA & Aaozai",
            gemstone = "Ruby / Garnet",
            intentionPhrase = "EDLPRNAA FIRE KING",
            wheelColorHex = "#FF5252"
        ),
        EnochianSigilEntry(
            id = 4,
            name = "Sigil of Raagiosl (West King)",
            category = "Watchtower Kings",
            traditionalMeaning = "The fluid azure sigil derived from tracing RAAGIOSL on the Water Watchtower wheel.",
            purpose = "Deepening intuitive scrying vision, emotional purification, dream work, and West elemental harmony.",
            planet = "Venus in Water",
            element = "Water",
            angelicRuler = "King RAAGIOSL & Lsrahp",
            gemstone = "Sapphire / Aquamarine",
            intentionPhrase = "RAAGIOSL WATER KING",
            wheelColorHex = "#80D8FF"
        ),
        EnochianSigilEntry(
            id = 5,
            name = "Sigil of Iczhihal (North King)",
            category = "Watchtower Kings",
            traditionalMeaning = "The heavy emerald sigil formed by tracing ICZHIHAL across the Earth Watchtower wheel.",
            purpose = "Physical manifestation, sanctuary protection, material stability, and North elemental grounding.",
            planet = "Saturn in Earth",
            element = "Earth",
            angelicRuler = "King ICZHIHAL & Laxdiza",
            gemstone = "Emerald / Malachite",
            intentionPhrase = "ICZHIHAL EARTH KING",
            wheelColorHex = "#69F0AE"
        ),
        EnochianSigilEntry(
            id = 6,
            name = "Seal of EXARP (Spirit of Air)",
            category = "Tablet of Union",
            traditionalMeaning = "The holy name EXARP from the top row of the Tablet of Union binding Air to Spirit.",
            purpose = "Invocations of divine breath, spiritual inspiration, and high intellectual gnosis.",
            planet = "Mercury",
            element = "Spirit of Air",
            angelicRuler = "Archangel Raphael",
            gemstone = "Citrine",
            intentionPhrase = "EXARP SPIRIT AIR",
            wheelColorHex = "#D0BCFF"
        ),
        EnochianSigilEntry(
            id = 7,
            name = "Seal of HCOMA (Spirit of Water)",
            category = "Tablet of Union",
            traditionalMeaning = "The holy name HCOMA from the second row of the Tablet of Union binding Water to Spirit.",
            purpose = "Unlocking spiritual empathy, fluid grace, and cleansing astral sanctuaries.",
            planet = "Moon",
            element = "Spirit of Water",
            angelicRuler = "Archangel Gabriel",
            gemstone = "Moonstone",
            intentionPhrase = "HCOMA SPIRIT WATER",
            wheelColorHex = "#80D8FF"
        ),
        EnochianSigilEntry(
            id = 8,
            name = "Seal of NANTA (Spirit of Earth)",
            category = "Tablet of Union",
            traditionalMeaning = "The holy name NANTA from the third row of the Tablet of Union binding Earth to Spirit.",
            purpose = "Grounding divine blessing into physical reality, health, and endurance.",
            planet = "Saturn",
            element = "Spirit of Earth",
            angelicRuler = "Archangel Uriel",
            gemstone = "Onyx",
            intentionPhrase = "NANTA SPIRIT EARTH",
            wheelColorHex = "#69F0AE"
        ),
        EnochianSigilEntry(
            id = 9,
            name = "Seal of BITOM (Spirit of Fire)",
            category = "Tablet of Union",
            traditionalMeaning = "The holy name BITOM from the fourth row of the Tablet of Union binding Fire to Spirit.",
            purpose = "Energetic activation, burning away spiritual blockages, and divine passion.",
            planet = "Sun",
            element = "Spirit of Fire",
            angelicRuler = "Archangel Michael",
            gemstone = "Carnelian",
            intentionPhrase = "BITOM SPIRIT FIRE",
            wheelColorHex = "#FF5252"
        ),
        EnochianSigilEntry(
            id = 10,
            name = "Seal of the 30 Aethyrs (LIL)",
            category = "Aethyr Seals",
            traditionalMeaning = "The crown sigil of the First Aethyr LIL representing supreme unity and illumination.",
            purpose = "Higher dimensional meditation, transcending duality, and receiving cosmic wisdom.",
            planet = "Kether / Crown Sol",
            element = "Spirit",
            angelicRuler = "Aethyr Governors of LIL",
            gemstone = "Diamond / Clear Quartz",
            intentionPhrase = "LIL FIRST AETHYR",
            wheelColorHex = "#D0BCFF"
        )
    )

    val PLANETARY_CORRESPONDENCES = listOf(
        PlanetaryCorrespondence(
            id = 1,
            planetName = "Sun",
            symbol = "☉",
            dayOfWeek = "Sunday",
            element = "Fire / Spirit",
            enochianSenior = "Habioro (Air Senior of Sun)",
            angelicRuler = "Archangel Michael & Semeliel",
            metal = "Pure Gold",
            incense = "Frankincense & Myrrh",
            gemstone = "Chrysolite / Diamond",
            colorHex = "#FFD54F",
            magicalDomain = "High spiritual enlightenment, sovereignty, vitality, illumination, and divine authority.",
            enochianCallsAssociated = "1st Key (Spirit), 2nd Key (Dominion)",
            sigilPhrase = "SOLAR SPIRIT ILLUMINATION"
        ),
        PlanetaryCorrespondence(
            id = 2,
            planetName = "Moon",
            symbol = "☽",
            dayOfWeek = "Monday",
            element = "Water",
            enochianSenior = "Aaozai (Fire Senior of Moon)",
            angelicRuler = "Archangel Gabriel & Levana",
            metal = "Fine Silver",
            incense = "Jasmine & Camphor",
            gemstone = "Moonstone / Pearl",
            colorHex = "#80D8FF",
            magicalDomain = "Astramagical scrying, dream vision, intuitive receptive channels, and subconscious cleansing.",
            enochianCallsAssociated = "3rd Key (Water Watchtower), 18th Key",
            sigilPhrase = "LUNAR SCRYING VISION"
        ),
        PlanetaryCorrespondence(
            id = 3,
            planetName = "Mercury",
            symbol = "☿",
            dayOfWeek = "Wednesday",
            element = "Air",
            enochianSenior = "Gebabal (Water Senior of Mercury)",
            angelicRuler = "Archangel Raphael & Madimi",
            metal = "Quicksilver / Electrum",
            incense = "Mastic & Storax",
            gemstone = "Opal / Agate",
            colorHex = "#D0BCFF",
            magicalDomain = "Enochian alphabet gematria, sigil tracing, rapid intellect, mental focus, and angelic communication.",
            enochianCallsAssociated = "4th Key (Air Watchtower), EXARP Seal",
            sigilPhrase = "MERCURIAL GEMATRIA WISDOM"
        ),
        PlanetaryCorrespondence(
            id = 4,
            planetName = "Venus",
            symbol = "♀",
            dayOfWeek = "Friday",
            element = "Water / Air",
            enochianSenior = "Lsrahp (Earth Senior of Venus)",
            angelicRuler = "Archangel Anael & Hagiel",
            metal = "Refined Copper",
            incense = "Rose & Benzoin",
            gemstone = "Emerald / Malachite",
            colorHex = "#69F0AE",
            magicalDomain = "Harmonizing astral sanctuaries, spiritual affinity, aesthetic perfection, and energetic cohesion.",
            enochianCallsAssociated = "5th Key, HCOMA Seal",
            sigilPhrase = "VENUSIAN ASTRAL HARMONY"
        ),
        PlanetaryCorrespondence(
            id = 5,
            planetName = "Mars",
            symbol = "♂",
            dayOfWeek = "Tuesday",
            element = "Fire",
            enochianSenior = "Saiinou (Water Senior of Mars)",
            angelicRuler = "Archangel Kamael & Graphiel",
            metal = "Tempered Iron / Steel",
            incense = "Dragon's Blood & Tobacco",
            gemstone = "Ruby / Garnet",
            colorHex = "#FF5252",
            magicalDomain = "Spiritual courage, burning away astral parasites, dynamic willpower, and sanctuary protection.",
            enochianCallsAssociated = "6th Key (Fire Watchtower), BITOM Seal",
            sigilPhrase = "MARTIAL FIRE PROTECTION"
        ),
        PlanetaryCorrespondence(
            id = 6,
            planetName = "Jupiter",
            symbol = "♃",
            dayOfWeek = "Thursday",
            element = "Air / Earth",
            enochianSenior = "Laxdiza (Earth Senior of Jupiter)",
            angelicRuler = "Archangel Sachiel & Johphiel",
            metal = "Pure Tin",
            incense = "Cedar & Saffron",
            gemstone = "Amethyst / Sapphire",
            colorHex = "#FFE082",
            magicalDomain = "Divine blessings, expansion, spiritual abundance, high wisdom, and sovereign Watchtower Kings.",
            enochianCallsAssociated = "7th Key, Watchtower Kings Invocations",
            sigilPhrase = "JUPITERIAN ABUNDANCE BLESSING"
        ),
        PlanetaryCorrespondence(
            id = 7,
            planetName = "Saturn",
            symbol = "♄",
            dayOfWeek = "Saturday",
            element = "Earth / Spirit",
            enochianSenior = "Slgaiol (Air Senior of Saturn)",
            angelicRuler = "Archangel Cassiel & Zaphkiel",
            metal = "Dense Lead / Obsidian",
            incense = "Myrrh & Cypress",
            gemstone = "Onyx / Jet / Black Tourmaline",
            colorHex = "#B0BEC5",
            magicalDomain = "Bounding astral dimensions, time mastery, discipline, sealing magic circle, and Aethyr ascension.",
            enochianCallsAssociated = "8th Key, NANTA Seal, 30 Aethyrs Invocations",
            sigilPhrase = "SATURNIAN SANCTUARY SEAL"
        )
    )
}


