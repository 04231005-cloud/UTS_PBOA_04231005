// ============================================================
//  Lab-Reserve — Peminjaman Alat Laboratorium
//  Kotlin Console Script (tanpa UI Android)
// ============================================================

enum class StatusPinjam {
    MENUNGGU_VERIFIKASI,
    DISETUJUI,
    DITOLAK,
    DIKEMBALIKAN
}

class AlatLab(
    val id: Int,
    namaAlat: String,
    kondisi: String,
    stok: Int
) {
    var namaAlat: String = namaAlat
        private set
    var kondisi: String = kondisi
        private set
    var stok: Int = stok
        private set

    fun ubahKondisi(kondisiBaru: String) {
        val valid = listOf("Baik", "Rusak", "Perbaikan")
        if (kondisiBaru !in valid) {
            println("  [ERROR] Kondisi '$kondisiBaru' tidak valid. Pilih: ${valid.joinToString()}")
            return
        }
        kondisi = kondisiBaru
        println("  [INFO] Kondisi alat '$namaAlat' diubah menjadi '$kondisi'.")
    }

    fun tambahStok(jumlah: Int) {
        if (jumlah <= 0) {
            println("  [ERROR] Jumlah tambah stok harus > 0.")
            return
        }
        stok += jumlah
        println("  [INFO] Stok '$namaAlat' ditambah $jumlah. Stok sekarang: $stok.")
    }

    fun kurangiStok(jumlah: Int): Boolean {
        if (jumlah <= 0) {
            println("  [ERROR] Jumlah kurang stok harus > 0.")
            return false
        }
        if (stok < jumlah) {
            println("  [ERROR] Stok tidak mencukupi (stok=$stok, diminta=$jumlah).")
            return false
        }
        stok -= jumlah
        return true
    }

    fun kembalikanStok(jumlah: Int) {
        stok += jumlah
        println("  [INFO] Stok '$namaAlat' dikembalikan +$jumlah. Stok sekarang: $stok.")
    }

    fun cekKetersediaan(): Boolean {
        return kondisi != "Rusak" && stok > 0
    }

    fun lihatInfo() {
        println("  └─ [Alat #$id] $namaAlat | Kondisi: $kondisi | Stok: $stok")
    }
}

class CatatanPinjam(
    val id: Int,
    val alat: AlatLab,
    val praktikan: Praktikan,
    jumlah: Int
) {
    var jumlah: Int = jumlah
        private set
    var status: StatusPinjam = StatusPinjam.MENUNGGU_VERIFIKASI
        private set

    fun setujui() {
        if (status != StatusPinjam.MENUNGGU_VERIFIKASI) {
            println("  [ERROR] Catatan pinjam #$id sudah berstatus '$status'.")
            return
        }
        if (!alat.kurangiStok(jumlah)) return
        status = StatusPinjam.DISETUJUI
        println("  [OK] Peminjaman #$id DISETUJUI — ${alat.namaAlat} x$jumlah untuk ${praktikan.nama}.")
    }

    fun tolak(alasan: String) {
        if (status != StatusPinjam.MENUNGGU_VERIFIKASI) {
            println("  [ERROR] Catatan pinjam #$id sudah berstatus '$status'.")
            return
        }
        status = StatusPinjam.DITOLAK
        println("  [INFO] Peminjaman #$id DITOLAK. Alasan: $alasan.")
    }

    fun kembalikan() {
        if (status != StatusPinjam.DISETUJUI) {
            println("  [ERROR] Hanya peminjaman berstatus DISETUJUI yang bisa dikembalikan.")
            return
        }
        alat.kembalikanStok(jumlah)
        status = StatusPinjam.DIKEMBALIKAN
        println("  [OK] Peminjaman #$id DIKEMBALIKAN oleh ${praktikan.nama}.")
    }

    fun lihatInfo() {
        println("  └─ [Pinjam #$id] ${alat.namaAlat} x$jumlah | Status: $status | Praktikan: ${praktikan.nama}")
    }
}

class Praktikan(
    val id: Int,
    nama: String,
    nim: Long
) {
    var nama: String = nama
        private set
    var nim: Long = nim
        private set

    private val riwayatPinjam: MutableList<CatatanPinjam> = mutableListOf()

    fun lihatAlat(daftarAlat: List<AlatLab>) {
        println("\n  === Daftar Alat Lab (dilihat oleh: $nama) ===")
        if (daftarAlat.isEmpty()) println("  (Tidak ada alat tersedia)")
        else daftarAlat.forEach { it.lihatInfo() }
    }

    fun pinjamAlat(alat: AlatLab, jumlah: Int, idCatatan: Int): CatatanPinjam? {
        println("\n  >> $nama mencoba meminjam '${alat.namaAlat}' x$jumlah ...")
        if (alat.kondisi == "Rusak") {
            println("  [GAGAL] Alat '${alat.namaAlat}' dalam kondisi RUSAK. Peminjaman ditolak.")
            return null
        }
        if (alat.stok == 0) {
            println("  [GAGAL] Stok alat '${alat.namaAlat}' HABIS (stok=0). Peminjaman ditolak.")
            return null
        }
        if (jumlah > alat.stok) {
            println("  [GAGAL] Jumlah pinjam ($jumlah) melebihi stok (${alat.stok}). Peminjaman ditolak.")
            return null
        }
        if (jumlah <= 0) {
            println("  [GAGAL] Jumlah pinjam harus > 0.")
            return null
        }
        val catatan = CatatanPinjam(idCatatan, alat, this, jumlah)
        riwayatPinjam.add(catatan)
        println("  [INFO] Pengajuan peminjaman berhasil dibuat. Status: MENUNGGU VERIFIKASI Laboran.")
        return catatan
    }

    fun lihatRiwayat() {
        println("\n  === Riwayat Pinjam — $nama ===")
        if (riwayatPinjam.isEmpty()) println("  (Belum ada riwayat)")
        else riwayatPinjam.forEach { it.lihatInfo() }
    }
}

class Laboran(
    val id: Int,
    nama: String,
    nip: String
) {
    var nama: String = nama
        private set
    var nip: String = nip
        private set

    private val daftarAlat: MutableList<AlatLab> = mutableListOf()

    fun tambahAlat(alat: AlatLab) {
        daftarAlat.add(alat)
        println("  [INFO] Laboran '$nama' menambahkan alat '${alat.namaAlat}' ke inventaris.")
    }

    fun hapusAlat(alatId: Int) {
        val target = daftarAlat.find { it.id == alatId }
        if (target == null) {
            println("  [ERROR] Alat dengan ID $alatId tidak ditemukan.")
            return
        }
        daftarAlat.remove(target)
        println("  [INFO] Alat '${target.namaAlat}' berhasil dihapus dari inventaris.")
    }

    fun kelolaAlat() {
        println("\n  === Inventaris Alat (Laboran: $nama) ===")
        if (daftarAlat.isEmpty()) println("  (Inventaris kosong)")
        else daftarAlat.forEach { it.lihatInfo() }
    }

    fun verifikasiAlat(catatan: CatatanPinjam, setujui: Boolean, alasan: String = "") {
        println("\n  >> Laboran '$nama' memverifikasi peminjaman #${catatan.id} ...")
        if (setujui) catatan.setujui()
        else catatan.tolak(alasan)
    }

    fun getDaftarAlat(): List<AlatLab> = daftarAlat.toList()
}

fun main() {
    println("╔══════════════════════════════════════════════════════╗")
    println("║        LAB-RESERVE — Peminjaman Alat Laboratorium    ║")
    println("╚══════════════════════════════════════════════════════╝")

    val laboran = Laboran(id = 1, nama = "Ali Rizqi N", nip = "LB-001")
    val praktikan1 = Praktikan(id = 1, nama = "Rina Agustina", nim = 2301010001L)
    val praktikan2 = Praktikan(id = 2, nama = "Dimas Prasetyo", nim = 2301010002L)

    val alatMikroskop  = AlatLab(id = 1, namaAlat = "Mikroskop",    kondisi = "Baik",  stok = 3)
    val alatBunsen     = AlatLab(id = 2, namaAlat = "Bunsen Burner", kondisi = "Rusak", stok = 2)
    val alatPipet      = AlatLab(id = 3, namaAlat = "Pipet Tetes",  kondisi = "Baik",  stok = 0)
    val alatCentrifuge = AlatLab(id = 4, namaAlat = "Centrifuge",   kondisi = "Baik",  stok = 1)

    laboran.tambahAlat(alatMikroskop)
    laboran.tambahAlat(alatBunsen)
    laboran.tambahAlat(alatPipet)
    laboran.tambahAlat(alatCentrifuge)

    var idCatatan = 1

    println("╔══════════════════════════════════════════════════════╗")
    println("║                  SKENARIO GAGAL                      ║")
    println("╚══════════════════════════════════════════════════════╝")
    println("\n[KASUS 1] Praktikan meminjam alat RUSAK")
    praktikan1.pinjamAlat(alatBunsen, jumlah = 1, idCatatan = idCatatan++)

    println("\n[KASUS 2] Praktikan meminjam alat yang STOK-nya 0")
    praktikan1.pinjamAlat(alatPipet, jumlah = 1, idCatatan = idCatatan++)

    println("\n[KASUS 3] Praktikan meminjam melebihi jumlah stok")
    praktikan1.pinjamAlat(alatMikroskop, jumlah = 10, idCatatan = idCatatan++)

    println("\n[KASUS 4] Laboran mencoba ubah kondisi dengan nilai tidak valid")
    alatMikroskop.ubahKondisi("Hilang")

    println("\n[KASUS 5] Laboran mencoba tambah stok dengan nilai negatif")
    alatMikroskop.tambahStok(-5)

    println("╔══════════════════════════════════════════════════════╗")
    println("║                  SKENARIO BERHASIL                   ║")
    println("╚══════════════════════════════════════════════════════╝")

    println("\n[KASUS 6] Rina pinjam Mikroskop x2 → Laboran setujui")
    val catatan1 = praktikan1.pinjamAlat(alatMikroskop, jumlah = 2, idCatatan = idCatatan++)
    catatan1?.let { laboran.verifikasiAlat(it, setujui = true) }

    println("\n[KASUS 7] Dimas pinjam Centrifuge x1 → Laboran setujui")
    val catatan2 = praktikan2.pinjamAlat(alatCentrifuge, jumlah = 1, idCatatan = idCatatan++)
    catatan2?.let { laboran.verifikasiAlat(it, setujui = true) }

    println("\n[KASUS 8] Rina pinjam Mikroskop x2 lagi → Laboran tolak (stok kurang)")
    val catatan3 = praktikan1.pinjamAlat(alatMikroskop, jumlah = 2, idCatatan = idCatatan++)
    catatan3?.let { laboran.verifikasiAlat(it, setujui = false, alasan = "Stok tidak mencukupi") }

    println("\n[KASUS 9] Laboran update kondisi & stok alat")
    alatBunsen.ubahKondisi("Perbaikan")
    alatPipet.tambahStok(5)

    println("\n[KASUS 10] Rina mengembalikan Mikroskop")
    catatan1?.kembalikan()

    println("╔══════════════════════════════════════════════════════╗")
    println("║                  LAPORAN AKHIR                       ║")
    println("╚══════════════════════════════════════════════════════╝")

    laboran.kelolaAlat()
    praktikan1.lihatRiwayat()
    praktikan2.lihatRiwayat()

    println("\n╔══════════════════════════════════════════════════════╗")
    println("║              Simulasi Selesai                        ║")
    println("╚══════════════════════════════════════════════════════╝")
}