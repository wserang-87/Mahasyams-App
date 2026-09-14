package com.example.data.local

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DatabaseInitializer {

    suspend fun seedDatabase(database: AppDatabase) = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val oneDay = 24 * 60 * 60 * 1000L

        // 1. Initial Users (Admin, Guru, Siswa)
        val users = listOf(
            UserEntity(
                uid = "admin-01",
                nama = "Ustadzah Fatimah (Admin)",
                email = "admin@mahasyams.id",
                username = "admin",
                role = "ADMIN",
                kelas = "Pusat Mahasyams",
                guruPembimbing = "-",
                status = "Aktif",
                createdAt = now - (30 * oneDay)
            ),
            UserEntity(
                uid = "guru-01",
                nama = "Ustadz Ridwan Al-Khathath",
                email = "ridwan@mahasyams.id",
                username = "ridwan",
                role = "GURU",
                kelas = "Pengajar Utama",
                guruPembimbing = "-",
                status = "Aktif",
                createdAt = now - (25 * oneDay)
            ),
            UserEntity(
                uid = "siswa-01",
                nama = "Ahmad Al-Mubarok",
                email = "ahmad@mahasyams.id",
                username = "ahmad",
                role = "SISWA",
                kelas = "IX A",
                guruPembimbing = "Ustadz Ridwan Al-Khathath",
                status = "Aktif",
                createdAt = now - (15 * oneDay)
            ),
            UserEntity(
                uid = "siswa-02",
                nama = "Siti Maryam",
                email = "maryam@mahasyams.id",
                username = "maryam",
                role = "SISWA",
                kelas = "IX A",
                guruPembimbing = "Ustadz Ridwan Al-Khathath",
                status = "Aktif",
                createdAt = now - (12 * oneDay)
            ),
            UserEntity(
                uid = "siswa-03",
                nama = "Zaid bin Haritsah",
                email = "zaid@mahasyams.id",
                username = "zaid",
                role = "SISWA",
                kelas = "VIII B",
                guruPembimbing = "Ustadz Ridwan Al-Khathath",
                status = "Aktif",
                createdAt = now - (10 * oneDay)
            )
        )
        database.userDao().insertUsers(users)

        // 2. 7 Khat Types (Istilah Khat Masterplan)
        val khatList = listOf(
            KhatEntity(
                id = "naskhi",
                nama = "Khat Naskhi",
                namaArab = "خَطُّ النَّسْخِ",
                sejarah = "Disempurnakan oleh Ibnu Muqlah pada abad ke-10 M di Baghdad. Dinamakan Naskhi karena digunakan untuk menyalin (menasakh) mushaf Al-Qur'an dan naskah penting umat Islam.",
                karakteristik = "Bentuk huruf seimbang, jelas, tidak terlalu banyak variasi rumit, dan sangat mudah dibaca oleh semua kalangan umat.",
                ciriKhas = "Sudut pena berkisar antara 70-80 derajat, tarikan garis lembut, ketinggian alif sekitar 5 titik persegi (nuqthah).",
                tokoh = "Ibnu Muqlah, Ibnu Bawwab, Syauqi Efendi, Hasyim Muhammad Al-Baghdadi.",
                contohDeskripsi = "Mushaf standar Madinah dan buku pelajaran kaligrafi klasik.",
                contohTeksArab = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"
            ),
            KhatEntity(
                id = "tsuluts",
                nama = "Khat Tsuluts",
                namaArab = "خَطُّ الثُّلُثِ",
                sejarah = "Dikenal sebagai 'Ummu Khuthuth' (Ibu Segala Khat). Merupakan jenis tulisan paling prestisius, megah, dan menjadi tolok ukur kemahiran seorang khathath (seniman kaligrafi).",
                karakteristik = "Lentur, agung, anggun, dengan ornamen harakat dan tanda hiasan (zukhruf) yang memenuhi ruang kosong komposisi.",
                ciriKhas = "Kepala huruf alif memiliki 'tarwis' (jambul runcing), ketinggian alif 7-9 titik, tarikan qalam dinamis melengkung.",
                tokoh = "Yakut Al-Musta'shimi, Sami Efendi, Hamid Al-Amidi.",
                contohDeskripsi = "Kubah masjid, kiswah Ka'bah, plakat istana, dan cover mushaf agung.",
                contohTeksArab = "اقْرَأْ بِاسْمِ رَبِّكَ الَّذِي خَلَقَ"
            ),
            KhatEntity(
                id = "diwani",
                nama = "Khat Diwani",
                namaArab = "خَطُّ الدِّيوَانِي",
                sejarah = "Diciptakan dan dirahasiakan oleh birokrasi Daulah Utsmaniyyah pada masa Sultan Muhammad Al-Fatih oleh Ibrahim Munif untuk menulis surat kerajaan dan piagam resmi.",
                karakteristik = "Tulisan bersambung sangat lentur, meliuk rapat, elegan, tanpa harakat pada penulisan dasarnya.",
                ciriKhas = "Garis dasar (baseline) melengkung seperti perahu atau gelombang, ujung huruf menyatu ritmis.",
                tokoh = "Ibrahim Munif, Ghazlan Bey, Mustafa Izzat.",
                contohDeskripsi = "Dekrit kerajaan Utsmani, piagam penghargaan, dan ijazah sanad kaligrafi.",
                contohTeksArab = "ن وَالْقَلَمِ وَمَا يَسْطُرُونَ"
            ),
            KhatEntity(
                id = "diwani_jali",
                nama = "Khat Diwani Jali",
                namaArab = "خَطُّ الدِّيوَانِي الجَلِي",
                sejarah = "Pengembangan artistik dari Khat Diwani yang dipelopori Syahlan Pasha untuk menambah nilai kemewahan dan estetika dekoratif tingkat tinggi.",
                karakteristik = "Sangat rapat, padat, seluruh rongga kosong dipenuhi titik-titik kecil dan hiasan mutiara kaligrafi (harakat tazyiniyyah).",
                ciriKhas = "Bentuk tulisan menyerupai perahu, tetesan air, atau kubah, kaya akan tekstur visual.",
                tokoh = "Syahlan Pasha, Daqiq Zadah, Hasyim Al-Baghdadi.",
                contohDeskripsi = "Dekorasi dinding istana, karya seni pigura kontemporer Islami.",
                contohTeksArab = "إِنَّ مَعَ الْعُسْرِ يُسْرًا"
            ),
            KhatEntity(
                id = "farisi",
                nama = "Khat Farisi (Ta'liq)",
                namaArab = "خَطُّ الفَارِسِي",
                sejarah = "Berkembang di wilayah Persia (Iran, Pakistan, India) sejak abad ke-9 Hijriyah. Sangat populer untuk menulis karya sastra dan puisi Islam.",
                karakteristik = "Perbedaan dramatis antara tebal dan tipis, garis condong ke kanan bawah, tampak mengalir lembut seperti tarian pena.",
                ciriKhas = "Sudut pena miring tajam, jarak antar huruf renggang memanjang dengan keanggunan luar biasa.",
                tokoh = "Mir Ali Tabrizi, Imad Al-Hasani.",
                contohDeskripsi = "Karya sastra Jalaluddin Rumi, manuskrip puisi Persia, prasasti Taj Mahal.",
                contohTeksArab = "فَبِأَيِّ آلَاءِ رَبِّكُمَا تُكَذِّبَانِ"
            ),
            KhatEntity(
                id = "nastaliq",
                nama = "Khat Nasta'liq",
                namaArab = "خَطُّ النَّسْتَعْلِيق",
                sejarah = "Perpaduan sempurna antara kejelasan Khat Naskhi dan keindahan Khat Ta'liq (Naskh + Ta'liq = Nasta'liq).",
                karakteristik = "Harmonis, ritmis, memiliki ketukan lembut, sangat digemari di anak benua India dan Asia Tengah.",
                ciriKhas = "Huruf kaf dan lam memanjang landai, bentuk nun seperti cawan bulan sabit sempurna.",
                tokoh = "Sultan Ali Mashhadi, Mir Imad.",
                contohDeskripsi = "Kitab sastra klasik dan percetakan bahasa Urdu/Parsi.",
                contohTeksArab = "وَمَن يَتَّقِ اللَّهَ يَجْعَل لَّهُ مَخْرَجًا"
            ),
            KhatEntity(
                id = "kufi",
                nama = "Khat Kufi",
                namaArab = "خَطُّ الكُوفِي",
                sejarah = "Khat tertua dalam peradaban Islam yang berasal dari kota Kufah di Irak. Digunakan pada masa awal pembukuan mushaf oleh Khalifah Utsman bin Affan.",
                karakteristik = "Geometris, kokoh, berkarakter arsitektural dengan garis vertikal dan horizontal yang tegas dan bersudut siku.",
                ciriKhas = "Tidak menggunakan harakat awal, mudah dibentuk menjadi motif geometris labirin (Kufi Murabba) maupun flora (Kufi Muzahhar).",
                tokoh = "Kaligrafer era Khulafaur Rasyidin, Dinasti Umayyah & Abbasiyyah.",
                contohDeskripsi = "Mihrab masjid bersejarah, koin dinar kuno, dekorasi dinding kubah dan pualam.",
                contohTeksArab = "اللَّهُ نُورُ السَّمَاوَاتِ وَالْأَرْضِ"
            )
        )
        database.khatDao().insertAllKhat(khatList)

        // 3. 28 Huruf Hijaiyah
        // 3. 28 Huruf Hijaiyah untuk 7 Jenis Khat (Naskhi, Tsuluts, Diwani, Diwani Jali, Farisi, Nasta'liq, Kufi)
        val rawHijaiyah = listOf(
            Triple("ا", "Alif", "Tarikan vertikal lurus"),
            Triple("ب", "Ba", "Lengkungan perahu dengan 1 titik di bawah"),
            Triple("ت", "Ta", "Badan menyerupai Ba dengan 2 titik di atas"),
            Triple("ث", "Tsa", "Badan menyerupai Ba dengan 3 titik segitiga di atas"),
            Triple("ج", "Jim", "Kepala bergelombang dan perut melengkung"),
            Triple("ح", "Ha", "Bentuk sama seperti Jim tanpa titik"),
            Triple("خ", "Kha", "Bentuk sama seperti Jim dengan 1 titik di atas"),
            Triple("د", "Dal", "Tinggi seimbang dengan alas mendatar"),
            Triple("ذ", "Dzal", "Bentuk sama dengan Dal ditambah 1 titik"),
            Triple("ر", "Ra", "Kepala miring meluncur ke bawah garis"),
            Triple("ز", "Zay", "Bentuk sama dengan Ra ditambah 1 titik"),
            Triple("س", "Sin", "Tiga gigi berirama dengan mangkuk cawan melengkung"),
            Triple("ش", "Syin", "Gigi Sin dengan 3 titik di atasnya"),
            Triple("ص", "Shad", "Kepala lonjong naik lalu meluncur ke cawan"),
            Triple("ض", "Dhad", "Bentuk Shad dengan 1 titik di atas"),
            Triple("ط", "Tha", "Badan lonjong dipadu tiang Alif vertikal"),
            Triple("ظ", "Zha", "Bentuk Tha dengan 1 titik di kanan tiang"),
            Triple("ع", "'Ain", "Kepala paruh burung dengan perut melengkung"),
            Triple("غ", "Ghain", "Bentuk 'Ain dengan 1 titik di atas"),
            Triple("ف", "Fa", "Kepala bulat dengan leher pendek dan badan perahu"),
            Triple("ق", "Qaf", "Kepala bulat dengan mangkuk cawan tenggelam"),
            Triple("ك", "Kaf", "Tiang tegak panjang dengan alas mendatar dan isyarah"),
            Triple("ل", "Lam", "Tiang turun melengkung membentuk cawan elok"),
            Triple("م", "Mim", "Kepala segitiga lembut dengan ekor meluncur"),
            Triple("ن", "Nun", "Cawan setengah lingkaran dengan 1 titik tengah"),
            Triple("هـ", "Ha'", "Bentuk lingkaran ganda yang harmonis"),
            Triple("و", "Waw", "Kepala bulat seperti Fa dengan ekor meluncur"),
            Triple("ي", "Ya'", "Kepala meliuk anggun dengan perut cawan")
        )

        val khatStyles = listOf(
            Pair("Naskhi", "Ukuran 5 titik nuqthah, sudut qalam 70°-80°, luwes dan sangat jelas untuk mushaf."),
            Pair("Tsuluts", "Ukuran 7-9 titik nuqthah, bermahkota tarwisa di puncak alif, anggun megah bertabur hiasan."),
            Pair("Diwani", "Ukuran 6 titik nuqthah, garis meliuk lentur tanpa harakat, sudut qalam tajam khas Utsmani."),
            Pair("Diwani Jali", "Ukuran 7 titik padat, bertabur titik-titik ornamen tazyin dan harakat jali dekoratif."),
            Pair("Farisi", "Ukuran 3 titik pendek condong, miring dari kanan atas ke kiri bawah dengan kontras tebal-tipis dramatis."),
            Pair("Nasta'liq", "Ukuran 6 titik mengalir (rawan), cawan huruf Ba dan Sin luas memanjang elegan."),
            Pair("Kufi", "Geometri bersudut kaku proporsional, alif tegak lurus laksana pilar arsitektur Islam klasik.")
        )

        val letters = mutableListOf<HijaiyahEntity>()
        khatStyles.forEach { (khatName, khatRule) ->
            val khatKey = khatName.lowercase().replace(" ", "_").replace("'", "")
            rawHijaiyah.forEachIndexed { index, (symbol, name, baseDesc) ->
                val letterKey = name.lowercase().replace("'", "").replace("-", "")
                val uniqueId = "${letterKey}_$khatKey"
                val fileType = if (index % 2 == 0) "application/pdf" else "image/png"
                val fileExt = if (index % 2 == 0) "pdf" else "png"
                val fileName = "Kaidah_${name}_Khat_${khatName.replace(" ", "_")}.$fileExt"

                letters.add(
                    HijaiyahEntity(
                        id = uniqueId,
                        huruf = symbol,
                        nama = name,
                        khat = khatName,
                        tunggalTeks = symbol,
                        tunggalDesc = "Bentuk tunggal huruf $name dalam gaya $khatName.",
                        awalTeks = "${symbol}ـ",
                        awalDesc = "Menyambung dengan huruf sesudahnya di awal kata ($khatName).",
                        tengahTeks = "ـ${symbol}ـ",
                        tengahDesc = "Menyambung di antara dua huruf di tengah ($khatName).",
                        akhirTeks = "ـ${symbol}",
                        akhirDesc = "Menyambung di akhir kata dengan penutup tarikan ($khatName).",
                        catatanKaidah = "$baseDesc. Kaidah khusus $khatName: $khatRule",
                        fileUrl = "https://mahasyams.id/database/materi/$uniqueId.$fileExt",
                        fileName = fileName,
                        mimeType = fileType
                    )
                )
            }
        }
        database.hijaiyahDao().insertAllHijaiyah(letters)

        // 4. Sample Materi Pembelajaran (Materi Khat & Materi per Huruf)
        val materiList = listOf(
            MateriEntity(
                id = "mat-01",
                judul = "Buku Panduan Standar Kaidah 7 Khat Klasik (PDF Lengkap)",
                khatId = "naskhi",
                khatNama = "Khat Naskhi",
                hurufId = "",
                hurufNama = "",
                deskripsi = "Modul PDF komprehensif membedah anatomi, sudut qalam, dan ukuran titik nuqthah 7 jenis khat karya master kaligrafi Timur Tengah.",
                tipeSumber = "UPLOAD",
                fileUrl = "https://mahasyams.id/files/buku_panduan_7_khat.pdf",
                filePath = "mahasyams/materi/buku_panduan_7_khat.pdf",
                fileName = "Buku_Panduan_Standar_7_Khat.pdf",
                mimeType = "application/pdf",
                thumbnailUrl = "",
                urutan = 1,
                status = "Aktif",
                createdBy = "Ustadz Ridwan Al-Khathath",
                createdAt = now - (14 * oneDay)
            ),
            MateriEntity(
                id = "mat-02",
                judul = "Lembar Kaidah Huruf Alif & Ba pada 7 Jenis Khat (Gambar HD)",
                khatId = "tsuluts",
                khatNama = "Khat Tsuluts",
                hurufId = "alif",
                hurufNama = "Alif",
                deskripsi = "Gambar komparasi visual bentuk Alif dan Ba dari Naskhi, Tsuluts, Diwani, Farisi, hingga Kufi lengkap dengan penanda titik pensil.",
                tipeSumber = "UPLOAD",
                fileUrl = "https://mahasyams.id/files/lembar_alif_ba_7khat.png",
                filePath = "mahasyams/materi/lembar_alif_ba_7khat.png",
                fileName = "Lembar_Kaidah_Alif_Ba_7_Khat.png",
                mimeType = "image/png",
                thumbnailUrl = "",
                urutan = 2,
                status = "Aktif",
                createdBy = "Ustadz Ridwan Al-Khathath",
                createdAt = now - (10 * oneDay)
            ),
            MateriEntity(
                id = "mat-03",
                judul = "Kaidah Lengkungan Huruf Jim, Ha, & Kha Khat Diwani Jali (PDF)",
                khatId = "diwani_jali",
                khatNama = "Khat Diwani Jali",
                hurufId = "jim",
                hurufNama = "Jim",
                deskripsi = "Bedah tuntas tarikan kepala burung elang dan cawan perut huruf Jim beserta penempatan harakat hiasan (tazyin).",
                tipeSumber = "UPLOAD",
                fileUrl = "https://mahasyams.id/files/kaidah_jim_diwani_jali.pdf",
                filePath = "mahasyams/materi/kaidah_jim_diwani_jali.pdf",
                fileName = "Kaidah_Jim_Ha_Kha_Diwani_Jali.pdf",
                mimeType = "application/pdf",
                thumbnailUrl = "",
                urutan = 3,
                status = "Aktif",
                createdBy = "Ustadz Ridwan Al-Khathath",
                createdAt = now - (7 * oneDay)
            ),
            MateriEntity(
                id = "mat-04",
                judul = "Tutorial Video: Anatomi Tarikan Qalam Khat Farisi",
                khatId = "farisi",
                khatNama = "Khat Farisi",
                hurufId = "",
                hurufNama = "",
                deskripsi = "Panduan video langsung cara memiringkan sudut pena kaligrafi bambu untuk menghasilkan kontras garis meluncur khas Persia.",
                tipeSumber = "LINK",
                fileUrl = "https://www.youtube.com/watch?v=kYJjYy7-jH4",
                fileName = "Tutorial Video Kaligrafi Farisi",
                mimeType = "video/youtube",
                urutan = 4,
                status = "Aktif",
                createdBy = "Ustadz Ridwan Al-Khathath",
                createdAt = now - (4 * oneDay)
            ),
            MateriEntity(
                id = "mat-05",
                judul = "Kaidah Geometri Huruf Dal & Kaf Khat Kufi Murabba (Gambar)",
                khatId = "kufi",
                khatNama = "Khat Kufi",
                hurufId = "dal",
                hurufNama = "Dal",
                deskripsi = "Pola grid kotak hitam-putih proporsional huruf Dal, Dzal, dan Kaf Kufi untuk prasasti arsitektur.",
                tipeSumber = "UPLOAD",
                fileUrl = "https://mahasyams.id/files/kufi_dal_kaf_grid.png",
                filePath = "mahasyams/materi/kufi_dal_kaf_grid.png",
                fileName = "Grid_Geometri_Kufi_Dal_Kaf.png",
                mimeType = "image/png",
                urutan = 5,
                status = "Aktif",
                createdBy = "Ustadz Ridwan Al-Khathath",
                createdAt = now - (2 * oneDay)
            )
        )
        database.materiDao().insertAllMateri(materiList)

        // 5. Sample Tugas Pembelajaran
        val tugasList = listOf(
            TugasEntity(
                id = "tugas-01",
                judul = "Latihan Huruf Ba, Ta, Tsa & Dal Khat Naskhi",
                deskripsi = "Tuliskan 3 baris huruf Ba, Ta, Tsa, dan Dal dalam bentuk tunggal dan bersambung. Perhatikan kedalaman perahu (1 titik) dan panjang badan (5 titik nuqthah). Unggah foto kertas karya Anda atau gambar menggunakan Qalam Digital di bawah.",
                materiId = "mat-01",
                khatId = "naskhi",
                guruId = "guru-01",
                guruNama = "Ustadz Ridwan Al-Khathath",
                mulai = now - (2 * oneDay),
                deadline = now + (3 * oneDay), // Sedang berlangsung
                sourceType = "LINK",
                fileUrl = "https://example.com/contoh_tugas_naskhi.jpg",
                fileName = "panduan_latihan_01.jpg",
                status = "Aktif",
                createdAt = now - (2 * oneDay)
            ),
            TugasEntity(
                id = "tugas-02",
                judul = "Penulisan Kalimat Basmalah Khat Tsuluts",
                deskripsi = "Susunlah lafadz Basmalah lengkap dengan tarwis huruf Alif & Lam dan harakat zukhruf khas Tsuluts. Kumpulkan sebelum batas akhir pengumpulan.",
                materiId = "mat-03",
                khatId = "tsuluts",
                guruId = "guru-01",
                guruNama = "Ustadz Ridwan Al-Khathath",
                mulai = now - (1 * oneDay),
                deadline = now + (7 * oneDay),
                sourceType = "UPLOAD",
                fileUrl = "",
                storagePath = "mahasyams/tugas/basmalah_template.png",
                fileName = "template_basmalah.png",
                status = "Aktif",
                createdAt = now - (1 * oneDay)
            )
        )
        database.tugasDao().insertAllTugas(tugasList)

        // 6. Sample Submissions (One already graded, one awaiting teacher correction!)
        val submissions = listOf(
            SubmissionEntity(
                id = "sub-01",
                tugasId = "tugas-01",
                tugasJudul = "Latihan Huruf Ba, Ta, Tsa & Dal Khat Naskhi",
                siswaId = "siswa-01",
                namaSiswa = "Ahmad Al-Mubarok",
                kelas = "IX A",
                khatId = "naskhi",
                fileUrl = "",
                drawingData = "STROKES:Naskhi-Ba-Sample",
                catatanSiswa = "Ustadz, mohon koreksi tarikan ekor huruf Ba pada baris kedua.",
                fileName = "karya_ahmad_naskhi.png",
                mimeType = "image/png",
                submittedAt = now - (1 * oneDay),
                status = "Sudah Dikoreksi",
                nilai = 88,
                komentar = "Alhamdulillah tarikan garis sangat bersih! Proporsi huruf Ba sudah pas 5 titik nuqthah. Perhatikan konsistensi ketebalan di bagian sudut siku agar tarikan semakin mantap. Terus berlatih!",
                guruKorektor = "Ustadz Ridwan Al-Khathath",
                dikoreksiAt = now - (12 * 60 * 60 * 1000L)
            ),
            SubmissionEntity(
                id = "sub-02",
                tugasId = "tugas-01",
                tugasJudul = "Latihan Huruf Ba, Ta, Tsa & Dal Khat Naskhi",
                siswaId = "siswa-02",
                namaSiswa = "Siti Maryam",
                kelas = "IX A",
                khatId = "naskhi",
                fileUrl = "",
                drawingData = "STROKES:Naskhi-Dal-Sample",
                catatanSiswa = "Bismillah, hasil latihan malam ini ustadz.",
                fileName = "karya_maryam_naskhi.png",
                mimeType = "image/png",
                submittedAt = now - (6 * 60 * 60 * 1000L),
                status = "Menunggu Koreksi",
                nilai = null,
                komentar = null,
                guruKorektor = null,
                dikoreksiAt = null
            )
        )
        database.submissionDao().insertAllSubmissions(submissions)

        // 7. Notifikasi Awal
        val notifications = listOf(
            NotifikasiEntity(
                id = "notif-01",
                userId = "siswa-01",
                judul = "Latihan Anda telah dikoreksi! 🎉",
                pesan = "Ustadz Ridwan Al-Khathath telah mengoreksi 'Latihan Huruf Ba & Dal' Anda dengan nilai 88.",
                tipe = "koreksi",
                isRead = false,
                createdAt = now - (12 * 60 * 60 * 1000L)
            ),
            NotifikasiEntity(
                id = "notif-02",
                userId = "ALL",
                judul = "Tugas Baru: Kalimat Basmalah Khat Tsuluts",
                pesan = "Materi dan tugas baru telah dibuka untuk seluruh santri/siswa kelas kaligrafi.",
                tipe = "tugas",
                isRead = false,
                createdAt = now - (1 * oneDay)
            )
        )
        database.notifikasiDao().insertAllNotifikasi(notifications)

        // 8. App Configuration
        val config = AppConfigEntity(
            id = 1,
            appName = "Mahasyams",
            appNameArab = "مَهَا شَمْس",
            tagline = "Belajar Kaligrafi, Menulis dengan Seni dan Adab",
            logoUrl = "",
            deskripsi = "Aplikasi Pembelajaran Kaligrafi Digital (Learning Management System Khat & Huruf Hijaiyah) untuk Guru dan Siswa Nusantara.",
            versi = "1.0.0",
            kontak = "admin@mahasyams.id"
        )
        database.appConfigDao().insertConfig(config)
    }
}
