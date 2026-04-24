// Class AlatLab
class AlatLab(
    val nama: String,
    var kondisi: String,
    var stok: Int
) {
    fun tersedia(): Boolean {
        return kondisi.lowercase() != "rusak" && stok > 0
    }

    fun kurangiStok() {
        if (stok > 0) stok--
    }

    fun tambahStok() {
        stok++
    }
}

// Class Praktikan
class Praktikan(val nama: String) {

    fun pinjamAlat(alat: AlatLab): Peminjaman? {
        println("$nama mencoba meminjam ${alat.nama}")

        return if (alat.tersedia()) {
            println("Pengajuan peminjaman berhasil, menunggu verifikasi laboran...")
            Peminjaman(this, alat)
        } else {
            println("Gagal meminjam: Alat rusak atau stok habis!")
            null
        }
    }
}

// Class Laboran
class Laboran(val nama: String) {

    fun verifikasi(peminjaman: Peminjaman) {
        println("Laboran $nama memverifikasi peminjaman...")

        if (peminjaman.alat.tersedia()) {
            peminjaman.status = "Disetujui"
            peminjaman.alat.kurangiStok()
            println("Peminjaman disetujui. Alat berhasil dipinjam.")
        } else {
            peminjaman.status = "Ditolak"
            println("Peminjaman ditolak. Alat tidak tersedia.")
        }
    }
}

// Class Peminjaman (Relasi)
class Peminjaman(
    val praktikan: Praktikan,
    val alat: AlatLab,
    var status: String = "Menunggu"
)

// Main Function
fun main() {
    // Data awal
    val alat1 = AlatLab("Multimeter", "Baik", 2)
    val alat2 = AlatLab("Oscilloscope", "Rusak", 1)

    val praktikan = Praktikan("Ali")
    val laboran = Laboran("Budi")

    // Kasus 1: Berhasil
    val peminjaman1 = praktikan.pinjamAlat(alat1)
    if (peminjaman1 != null) {
        laboran.verifikasi(peminjaman1)
        println("Status akhir: ${peminjaman1.status}")
    }

    println("\n----------------------\n")

    // Kasus 2: Gagal (alat rusak)
    val peminjaman2 = praktikan.pinjamAlat(alat2)
    if (peminjaman2 != null) {
        laboran.verifikasi(peminjaman2)
        println("Status akhir: ${peminjaman2.status}")
    }
}
asdadasdadasdasd