package com.aksekili.fithesap

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.aksekili.fithesap.databinding.ActivityMainBinding
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding //tanımladım
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater) //bağladım
        setContentView(binding.root) //gösterdim
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        binding.btnTemizle.setOnClickListener { temizle() }
        binding.btnHesapla.setOnClickListener { hesapla() }
    }

    // (b) Bir kutudaki sayıyı okur ve kontrol eder.
// Sorun varsa kutuda hata gösterir ve null döner.
    private fun sayiOku(kutu: EditText, min: Double, max: Double): Double? {
        val metin = kutu.text.toString().replace(',', '.')
        val sayi = metin.toDoubleOrNull() //ya double döndürecek ya da null döndürecek
        if (sayi == null) {
            kutu.error = "Bu alan boş bırakılamaz"
            return null
        }
        if (sayi < min || sayi > max) {
            kutu.error = "${min.roundToInt()} ile ${max.roundToInt()} arasında olmalıdır"
            return null
        }
        return sayi
    }
    // (c) Tüm hesapları yapar ve sonuç kartını doldurur
    @SuppressLint("SetTextI18n")
    private fun hesapla() {
        val kilo = sayiOku(binding.edtKilo, 20.0, 300.0)
        val boyCm = sayiOku(binding.edtBoy, 100.0, 250.0)
        val yas = sayiOku(binding.edtYas, 10.0, 100.0)
        if (kilo == null || boyCm == null || yas == null) {
            binding.cardSonuc.visibility = View.GONE
            return
        }
        val erkek = binding.rbErkek.isChecked //true veya false döndürecek
        val hareketKatsayisi = when (binding.rgAktivite.checkedRadioButtonId) //when ile seçim yapıyor
        {
            R.id.rbAz -> 1.2 //rbAz seçilirse 1.2 döndürecek
            R.id.rbOrta -> 1.55 //rbOrta seçilirse 1.55 döndürecek
            else -> 1.725 //diğer durumlarda 1.725 döndürecek
        }
        // 1) Vücut Kitle İndeksi
        val boyM = boyCm / 100 //cm olarak alınan boyu metreye dönüştürdük
        val vki = kilo / (boyM * boyM) //vki hesaplandı
// 2) İdeal kilo aralığı
        val idealEnAz = 18.5 * boyM * boyM
        val idealEnCok = 24.9 * boyM * boyM
// 3) Günlük kalori ihtiyacı
        val bazal = if (erkek) {
            10 * kilo + 6.25 * boyCm - 5 * yas + 5
        } else {
            10 * kilo + 6.25 * boyCm - 5 * yas - 161
        }
        val kalori = bazal * hareketKatsayisi //kalori hesaplandı
// 4) Günlük su ihtiyacı
        val suLitre = kilo * 0.033
// Sonuçları karta yaz
        binding.txtVki.text = "%.1f".format(vki) //%1.f demek virgülden sonra bir basamak göster demek
        binding.txtDurum.text = vkiDurumu(vki)
        binding.txtDurum.setTextColor(getColor(durumRengi(vki)))
        binding.txtIdeal.text = "İdeal kilo aralığı: %.0f – %.0f kg".format(idealEnAz, idealEnCok)
        binding.txtKalori.text = "Günlük kalori ihtiyacı: ${kalori.roundToInt()} kcal"
        binding.txtSu.text = "Günlük su ihtiyacı: %.1f litre".format(suLitre)
        binding.cardSonuc.visibility = View.VISIBLE //görünür yaptık
    }
    private fun vkiDurumu(vki: Double): String {
        return when {
            vki < 18.5 -> "Zayıf"
            vki < 25 -> "Normal"
            vki < 30 -> "Fazla kilolu"
            else -> "Obez"
        }
    }
    private fun durumRengi(vki: Double): Int {
        return when {
            vki < 18.5 -> R.color.durum_uyari
            vki < 25 -> R.color.durum_normal
            vki < 30 -> R.color.durum_uyari
            else -> R.color.durum_tehlike
        }
    }
    // (d) Ekranı açılıştaki haline döndürür
    private fun temizle() {
        binding.edtKilo.setText("")
        binding.edtBoy.setText("")
        binding.edtYas.setText("")
        binding.edtKilo.error = null //null olabiliyordu o yüzden null
        binding.edtBoy.error = null
        binding.edtYas.error = null
        binding.rgCinsiyet.check(R.id.rbKadin)
        binding.rgAktivite.check(R.id.rbOrta)
        binding.cardSonuc.visibility = View.GONE //görünmez yaptık
    }
}





