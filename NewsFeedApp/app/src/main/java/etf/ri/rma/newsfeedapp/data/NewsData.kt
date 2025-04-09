package etf.ri.rma.newsfeedapp.data

import androidx.compose.runtime.sourceInformationMarkerEnd
import etf.ri.rma.newsfeedapp.model.NewsItem

object NewsData {
    fun getAllNews(): List<NewsItem>{
        return listOf(
            NewsItem(
                id="1",
                title="Zmajevi osigurali prolaz na Svjetsko prvenstvo!",
                snippet= "Pobjedivši reprezentaciju Austrije, Bosna i Hercegovina je zauzela prvo mjesto u kvalifikacionoj grupi i oduševila naciju.",
                imageUrl = null,
                category = "Sport",
                isFeatured = true,
                source = "reprezentacija.ba",
                publishedDate = "17.2.2025"
            ),
            NewsItem(
                id="2",
                title="Košarkaši BiH briljirali protiv Litvanije",
                snippet="Nevjerovatna utakmica naših košarkaša u Sarajevu, ostvarena ubjedljiva pobjeda rezultatom 109:81",
                imageUrl=null,
                category="Sport",
                isFeatured = false,
                source = "sport.ba",
                publishedDate = "16.1.2025"
            ),
            NewsItem(
                id="3",
                title="Najbolja bh plivačica osvojila zlato na Olimpijskim igrama",
                snippet="Talentovana Lana Pudar osvojila je zlato i postavila novi svjetski rekord za 200m delfin.",
                imageUrl=null,
                category="Sport",
                isFeatured=true,
                source="tenis.ba",
                publishedDate="15.1.2025"
            ),
            NewsItem(
                id="4",
                title="Maraton u Mostaru: Rekordan broj učesnika",
                snippet="Više od 2000 trkača učestvovalo na ovogodišnjem maratonu.",
                imageUrl=null,
                category="Sport",
                isFeatured=false,
                source="hercegovinasport.ba",
                publishedDate="14.1.2025"
            ),
            NewsItem(
                id="5",
                title="Nova nada bh. fudbala prelazi u Bayern",
                snippet="Mladi napadač potpisao ugovor s njemačkim gigantom, očekuje se njegov nastup i za reprezentaciju BiH",
                imageUrl=null,
                category="Sport",
                isFeatured=true,
                source="klix.ba",
                publishedDate="13.3.2025"
            ),
            NewsItem(
                id="6",
                title="Potvrđena optužnica protiv ministra sigurnosti BiH",
                snippet = "Nakon tri mjeseca šutnje, potvrđene su informacije da je ministar sigurnosti pod lupom MUP-a Kantona Sarajevo",
                imageUrl = null,
                category = "Politika",
                isFeatured = false,
                source = "klix.ba",
                publishedDate="13.3.2025"

            ),
            NewsItem(
                id="7",
                title="Stranka demokratske akcije kažnjena zbog prijevremene izborne kampanje",
                snippet = "Prijava Stranke demokratske akcije stigla je zbog podjele EuroBlok čokoladica sa omotom njenih članova",
                imageUrl = null,
                category = "Politika",
                isFeatured = false,
                source = "klix.ba",
                publishedDate = "12.4.2025"
            ),
            NewsItem(
                id="8",
                title = "Visoka predstavnica EU stiže u Bosnu i Hercegovinu",
                snippet = "Visoka predstavnica EU ima dogovoren sastanak sa članovima Predsjedništva BiH, razgovarat će o sigurnosnoj politici BiH",
                imageUrl = null,
                category = "Politika",
                isFeatured = false,
                source = "klix.ba",
                publishedDate = "27.1.2025"
            ),
            NewsItem(
                id="9",
                title = "Izglasan zakon o neradnoj nedjelji u FBiH",
                snippet = "Zakon ni ovaj put ne vrijedi za cijelu Bosnu i Hercegovinu, zvaničnici RS bili su protiv",
                imageUrl = null,
                category = "Politika",
                isFeatured = true,
                source = "klix.ba",
                publishedDate = "3.1.2025"
            ),
            NewsItem(
                id="10",
                title = "Srednjoškolci iz BiH ostvarili značajne rezultate na svjetskom takmičenju iz matematike ",
                snippet = "Uspješni srednjoškolci vratili su se iz Pariza sa 11 osvojenih medalja, čime su osigurali prestižne stipendije za početak studiranja.",
                imageUrl = null,
                category = "Nauka/tehnologija",
                isFeatured = true,
                source = "avaz.ba",
                publishedDate = "24.3.2025"
            ),
            NewsItem(
                id="11",
                title="Bh. studenti osvojili zlato na svjetskom takmičenju iz robotike",
                snippet="Inovativni tim Elektrotehničkog fakulteta Sarajevo briljirao u Švicarskoj.",
                imageUrl=null,
                category="Nauka/tehnologija",
                isFeatured=true,
                source="tehno.ba",
                publishedDate="17.3.2025"
            ),
            NewsItem(
                id="12",
                title="Mostar dobija prvi tehnološki park u BiH",
                snippet="Projekat vrijedan 15 miliona eura okupit će startupe i inovatore iz cijele regije.",
                imageUrl=null,
                category="Nauka/tehnologija",
                isFeatured=false,
                source="startup.ba",
                publishedDate="20.1.2025"
            ),
            NewsItem(
                id="13",
                title="U Zenici otvoren prvi laboratorij za robotiku",
                snippet="Najmodernija oprema dostupna je studentima i istraživačima za razvoj naprednih robotskih sistema.",
                imageUrl=null,
                category="Nauka/tehnologija",
                isFeatured=false,
                source="tehno.ba",
                publishedDate="5.1.2025"
            ),
            NewsItem(
                id="14",
                title="Historijski uspjeh bh. tenisera na Roland Garrosu",
                snippet="Mladi teniser iz Banja Luke plasirao se među 8 najboljih na prestižnom Grand Slam turniru.",
                imageUrl=null,
                category="Sport",
                isFeatured=true,
                source="sportski.ba",
                publishedDate="1.4.2025"
            ),
            NewsItem(
                id="15",
                title="Rukometaši BiH izborili plasman na Evropsko prvenstvo",
                snippet="Nakon dramatične utakmice protiv Slovenije, naša reprezentacija osigurala mjesto među evropskom elitom.",
                imageUrl=null,
                category="Sport",
                isFeatured=true,
                source="sportskiportal.ba",
                publishedDate="10.4.2025"
            ),
            NewsItem(
                id="16",
                title="Fudbalska liga BiH: Spektakl u derbiju kola",
                snippet="Sarajevo i Željezničar remizirali u dramatičnoj utakmici.",
                imageUrl=null,
                category="Sport",
                isFeatured=false,
                source="sportsport.ba",
                publishedDate="2.1.2025"
            ),
            NewsItem(
                id="17",
                title="Predložene izmjene Ustava FBiH",
                snippet="Stručnjaci ukazuju na potrebu za reformama, zastarjeli Zakoni ne mogu donijeti napredak",
                imageUrl=null,
                category="Politika",
                isFeatured=false,
                source="klix.ba",
                publishedDate="1.2.2025"
            ),
            NewsItem(
                id="18",
                title="BiH uvodi predmet Programiranje u osnovne škole",
                snippet="Djeca će već od petog razreda učiti osnove kodiranja.",
                imageUrl=null,
                category="Nauka/tehnologija",
                isFeatured=true,
                source="edukacija.ba",
                publishedDate="30.1.2025"
            ),
            NewsItem(
                id="19",
                title="Bh. aplikacija za učenje jezika osvojila međunarodnu nagradu",
                snippet="Inovativni softver koristi vještačku inteligenciju za personalizovane lekcije prilagođene svakom korisniku.",
                imageUrl=null,
                category="Nauka/tehnologija",
                isFeatured=false,
                source="tehno.ba",
                publishedDate="15.1.2025"
            ),
            NewsItem(
                id="20",
                title="Izmjena izbornog zakona: Uvedeno je elektronsko glasanje",
                snippet="Od 2026. godine, bit će smanjena stopa propusta u glasanju za državne službenike zahvaljujući uvođenju elektronskog glasanja.",
                imageUrl=null,
                category="Politika",
                isFeatured=false,
                source="klix.ba",
                publishedDate="28.3.2025"
            )
        )
    }
}