package za.co.iclips.bustracker.bustracker;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class Polylines {
    private static  PolylineOptions line_cbd_ndp = new PolylineOptions()
                .add(
                new LatLng(-33.99987,22.44601),new LatLng(-34.00215,22.44691),
                new LatLng(-34.00232,22.44698),new LatLng(-34.00267,22.4471),
                new LatLng(-34.0043,22.44766),new LatLng(-34.00441,22.44765),
                new LatLng(-34.00451,22.44769),new LatLng(-34.01146,22.45023),
                new LatLng(-34.01154,22.45029),new LatLng(-34.01157,22.45031),
                new LatLng(-34.01166,22.45034),new LatLng(-34.01247,22.45059),
                new LatLng(-34.0126,22.45062),new LatLng(-34.01272,22.45065),
                new LatLng(-34.01492,22.45102),new LatLng(-34.01492,22.45104),
                new LatLng(-34.0155,22.45347),new LatLng(-34.01575,22.45455),
                new LatLng(-34.01622,22.45663),new LatLng(-34.01627,22.45681),
                new LatLng(-34.01647,22.45744),new LatLng(-34.01669,22.45815),
                new LatLng(-34.01671,22.45826),new LatLng(-34.01674,22.45848),
                new LatLng(-34.01675,22.45866),new LatLng(-34.01675,22.4589),
                new LatLng(-34.01664,22.45941),new LatLng(-34.0166,22.45985),
                new LatLng(-34.01661,22.46013),new LatLng(-34.01667,22.46031),
                new LatLng(-34.01683,22.46098),new LatLng(-34.01701,22.46178),
                new LatLng(-34.01705,22.46187),new LatLng(-34.01747,22.46287),
                new LatLng(-34.01748,22.46291),new LatLng(-34.01752,22.46311),
                new LatLng(-34.01799,22.46517),new LatLng(-34.01796,22.4655),
                new LatLng(-34.01777,22.46774),new LatLng(-34.01777,22.46785),
                new LatLng(-34.01778,22.46797),new LatLng(-34.01783,22.46822),
                new LatLng(-34.01812,22.46953),new LatLng(-34.01845, 22.47076),

                new LatLng(-34.01901, 22.47116),new LatLng(-34.01976, 22.47099),
                new LatLng(-34.01983, 22.47165),new LatLng(-34.01976, 22.47214),
                new LatLng(-34.0196, 22.47255),new LatLng(-34.01925, 22.473),
                new LatLng(-34.01914, 22.47357),new LatLng(-34.0192, 22.47387),
                new LatLng(-34.01944, 22.4747),new LatLng(-34.02045, 22.47515),
                new LatLng(-34.02057, 22.47544),new LatLng(-34.02069, 22.47599),

                new LatLng(-34.01974,22.4774),
                new LatLng(-34.01921,22.47759),
                new LatLng(-34.01912,22.47763),
                new LatLng(-34.01892,22.47772),
                new LatLng(-34.01867,22.47783),
                new LatLng(-34.01837,22.47781),
                new LatLng(-34.018,22.47778),
                new LatLng(-34.01766,22.47775),
                new LatLng(-34.01743,22.47774),
                new LatLng(-34.01718,22.47772),
                new LatLng(-34.01686,22.4777),
                new LatLng(-34.01673,22.47769),
                new LatLng(-34.01655,22.47767),
                new LatLng(-34.01652,22.47767),
                new LatLng(-34.01649,22.47765),
                new LatLng(-34.01632,22.47748),
                new LatLng(-34.01618,22.47734),
                new LatLng(-34.01614,22.4773),
                new LatLng(-34.0161,22.47726),
                new LatLng(-34.01606,22.47722),
                new LatLng(-34.01603,22.47719),
                new LatLng(-34.01599,22.47715),
                new LatLng(-34.01595,22.47711),
                new LatLng(-34.01592,22.47708),
                new LatLng(-34.01588,22.47703),
                new LatLng(-34.01587,22.47702),
                new LatLng(-34.01587,22.47701),
                new LatLng(-34.01587,22.47697),
                new LatLng(-34.01586,22.47684),
                new LatLng(-34.01586,22.47676),
                new LatLng(-34.01586,22.47666),
                new LatLng(-34.01583,22.47599),
                new LatLng(-34.01583,22.47596),
                new LatLng(-34.01584,22.47586),
                new LatLng(-34.01584,22.47583),
                new LatLng(-34.01585,22.47578),
                new LatLng(-34.01585,22.47576),
                new LatLng(-34.01586,22.47574),
                new LatLng(-34.01587,22.47572),
                new LatLng(-34.01659,22.47383),
                new LatLng(-34.01659,22.47382),
                new LatLng(-34.0166,22.4738),
                new LatLng(-34.01662,22.47361),
                new LatLng(-34.01668,22.4732),
                new LatLng(-34.01669,22.47316),
                new LatLng(-34.0167,22.47308),
                new LatLng(-34.01675,22.47281),
                new LatLng(-34.01677,22.47272),
                new LatLng(-34.01694,22.47174),
                new LatLng(-34.01696,22.47164),
                new LatLng(-34.01701,22.47136),
                new LatLng(-34.0172,22.4702),
                new LatLng(-34.0172,22.47017),
                new LatLng(-34.01721,22.47014),
                new LatLng(-34.01722,22.4701),
                new LatLng(-34.01724,22.47007),
                new LatLng(-34.01728,22.46998),
                new LatLng(-34.0173,22.46994),
                new LatLng(-34.01731,22.46992),
                new LatLng(-34.01734,22.4699),
                new LatLng(-34.01738,22.46987),
                new LatLng(-34.01749,22.46979),
                new LatLng(-34.0175,22.46978),
                new LatLng(-34.01753,22.46977),
                new LatLng(-34.01756,22.46976),
                new LatLng(-34.01761,22.46974),
                new LatLng(-34.01769,22.46971),
                new LatLng(-34.01813,22.46954),
                new LatLng(-34.01812,22.46947),
                new LatLng(-34.01799,22.46891),
                new LatLng(-34.01779,22.46797),
                new LatLng(-34.01778,22.46794),
                new LatLng(-34.01778,22.46788),
                new LatLng(-34.01777,22.46784),
                new LatLng(-34.01777,22.46778),
                new LatLng(-34.01777,22.46773),
                new LatLng(-34.01778,22.46769),
                new LatLng(-34.01779,22.46754),
                new LatLng(-34.01795,22.46569),
                new LatLng(-34.01799,22.46517),
                new LatLng(-34.01799,22.46513),
                new LatLng(-34.01748,22.46287),
                new LatLng(-34.01746,22.46283),
                new LatLng(-34.01701,22.46178),
                new LatLng(-34.017,22.46173),
                new LatLng(-34.01668,22.46033),
                new LatLng(-34.01667,22.46028),
                new LatLng(-34.01662,22.46014),
                new LatLng(-34.01662,22.46012),
                new LatLng(-34.01661,22.46007),
                new LatLng(-34.01661,22.45987),
                new LatLng(-34.01661,22.45985),
                new LatLng(-34.01661,22.45982),
                new LatLng(-34.01665,22.45941),
                new LatLng(-34.01666,22.45934),
                new LatLng(-34.01675,22.45891),
                new LatLng(-34.01676,22.4588),
                new LatLng(-34.01676,22.45869),
                new LatLng(-34.01676,22.45861),
                new LatLng(-34.01675,22.45848),
                new LatLng(-34.01674,22.45842),
                new LatLng(-34.01671,22.45823),
                new LatLng(-34.0167,22.45818),
                new LatLng(-34.01669,22.45813),
                new LatLng(-34.01658,22.45775),
                new LatLng(-34.0164,22.45721),
                new LatLng(-34.01638,22.45714),
                new LatLng(-34.01625,22.45674),
                new LatLng(-34.01624,22.45671),
                new LatLng(-34.01604,22.45578),
                new LatLng(-34.016,22.45563),
                new LatLng(-34.01569,22.45427),
                new LatLng(-34.01567,22.45419),
                new LatLng(-34.01519,22.45207),
                new LatLng(-34.0149,22.45091),
                new LatLng(-34.01401,22.45077),
                new LatLng(-34.01396,22.45076),
                new LatLng(-34.0126,22.4505),
                new LatLng(-34.01256,22.45049),
                new LatLng(-34.01247,22.45047),
                new LatLng(-34.0116,22.45021),
                new LatLng(-34.01155,22.45021),
                new LatLng(-34.01146,22.45022),
                new LatLng(-34.01144,22.45021),
                new LatLng(-34.00519,22.44793),
                new LatLng(-34.00472,22.44776),
                new LatLng(-34.00449,22.44768),
                new LatLng(-34.00441,22.44765),
                new LatLng(-34.00439,22.44763),
                new LatLng(-34.00433,22.44758),
                new LatLng(-34.00429,22.44757),
                new LatLng(-34.00277,22.44703),
                new LatLng(-34.00269,22.447),
                new LatLng(-33.99927,22.44566),
                new LatLng(-33.99922,22.44564),
                new LatLng(-33.99855,22.44542),
                new LatLng(-33.9984,22.44537),
                new LatLng(-33.99815,22.44528),
                new LatLng(-33.99811,22.44526),
                new LatLng(-33.99793,22.4452),
                new LatLng(-33.99778,22.44514),
                new LatLng(-33.99749,22.44503),
                new LatLng(-33.99742,22.445),
                new LatLng(-33.99712,22.44488),
                new LatLng(-33.99708,22.44487),
                new LatLng(-33.99665,22.44472),
                new LatLng(-33.99648,22.44466),
                new LatLng(-33.99402,22.44383),
                new LatLng(-33.99396,22.44381),
                new LatLng(-33.99388,22.44378),
                new LatLng(-33.99376,22.44374),
                new LatLng(-33.99318,22.44354),
                new LatLng(-33.99282,22.44341),
                new LatLng(-33.99257,22.44333),
                new LatLng(-33.99219,22.44319),
                new LatLng(-33.99217,22.44319),
                new LatLng(-33.99211,22.44319),
                new LatLng(-33.99209,22.44319),
                new LatLng(-33.992,22.44316),
                new LatLng(-33.98838,22.44187),
                new LatLng(-33.98831,22.44185),
                new LatLng(-33.98764,22.4417),
                new LatLng(-33.98761,22.4417),
                new LatLng(-33.98753,22.44169),
                new LatLng(-33.98377,22.44125),
                new LatLng(-33.98367,22.44121),
                new LatLng(-33.98359,22.44119),
                new LatLng(-33.98264,22.44106),
                new LatLng(-33.98262,22.44101),
                new LatLng(-33.98259,22.44097),
                new LatLng(-33.98256,22.44094),
                new LatLng(-33.98253,22.44092),
                new LatLng(-33.98249,22.44091),
                new LatLng(-33.98243,22.44091),
                new LatLng(-33.98239,22.44091),
                new LatLng(-33.98234,22.44094),
                new LatLng(-33.9823,22.44098),
                new LatLng(-33.98228,22.44102),
                new LatLng(-33.98223,22.44102),
                new LatLng(-33.98202,22.44101),
                new LatLng(-33.98193,22.441),
                new LatLng(-33.98159,22.44096),
                new LatLng(-33.98144,22.44094),
                new LatLng(-33.98139,22.44093),
                new LatLng(-33.98061,22.44086),
                new LatLng(-33.98056,22.44086),
                new LatLng(-33.98033,22.44086),
                new LatLng(-33.9803,22.44086),
                new LatLng(-33.97996,22.4409),
                new LatLng(-33.9799,22.44091),
                new LatLng(-33.97948,22.44099),
                new LatLng(-33.97946,22.441),
                new LatLng(-33.97943,22.44101),
                new LatLng(-33.97904,22.44112),
                new LatLng(-33.979,22.44114),
                new LatLng(-33.97833,22.44143),
                new LatLng(-33.97832,22.44143),
                new LatLng(-33.9783,22.44144),
                new LatLng(-33.97807,22.44158),
                new LatLng(-33.97803,22.44161),
                new LatLng(-33.97794,22.44167),
                new LatLng(-33.96936,22.44838),
                new LatLng(-33.96929,22.44844),
                new LatLng(-33.9668,22.45041),
                new LatLng(-33.96671,22.45047),
                new LatLng(-33.96558,22.45129),
                new LatLng(-33.96554,22.45132),
                new LatLng(-33.95745,22.45771),
                new LatLng(-33.9571,22.45798),
                new LatLng(-33.95681,22.45819),
                new LatLng(-33.95664,22.45829),
                new LatLng(-33.95638,22.45842),
                new LatLng(-33.95624,22.45849),
                new LatLng(-33.95603,22.45863),
                new LatLng(-33.95591,22.45871),
                new LatLng(-33.95578,22.45883),
                new LatLng(-33.95576,22.45884),
                new LatLng(-33.95571,22.4589),
                new LatLng(-33.95559,22.45906),
                new LatLng(-33.95556,22.45905),
                new LatLng(-33.95553,22.45905),
                new LatLng(-33.9555,22.45905),
                new LatLng(-33.95546,22.45907),
                new LatLng(-33.95543,22.45909),
                new LatLng(-33.95538,22.45914),
                new LatLng(-33.95534,22.45922),
                new LatLng(-33.95533,22.45926),
                new LatLng(-33.95533,22.45931),
                new LatLng(-33.95534,22.45935),
                new LatLng(-33.95538,22.45944),
                new LatLng(-33.9554,22.45947),
                new LatLng(-33.95542,22.45949),
                new LatLng(-33.95543,22.45952),
                new LatLng(-33.95543,22.45958),
                new LatLng(-33.95544,22.45975),
                new LatLng(-33.95545,22.45982),
                new LatLng(-33.95546,22.45989),
                new LatLng(-33.95549,22.45995),
                new LatLng(-33.95562,22.46019),
                new LatLng(-33.95573,22.46039),
                new LatLng(-33.95609,22.46101),
                new LatLng(-33.95611,22.46105),
                new LatLng(-33.95699,22.4627),
                new LatLng(-33.95705,22.4628),
                new LatLng(-33.9573,22.46325),
                new LatLng(-33.95739,22.4634),
                new LatLng(-33.95744,22.4635),
                new LatLng(-33.95756,22.46372),
                new LatLng(-33.95789,22.46432),
                new LatLng(-33.95802,22.46457),
                new LatLng(-33.95816,22.46482),
                new LatLng(-33.95826,22.465),
                new LatLng(-33.95838,22.46523),
                new LatLng(-33.95847,22.46541),
                new LatLng(-33.95852,22.4655),
                new LatLng(-33.95858,22.4656),
                new LatLng(-33.95863,22.46569),
                new LatLng(-33.95869,22.4658),
                new LatLng(-33.96024,22.46861),
                new LatLng(-33.96029,22.4687),
                new LatLng(-33.96031,22.46875),
                new LatLng(-33.96053,22.46933),
                new LatLng(-33.96056,22.4694),
                new LatLng(-33.96058,22.46947),
                new LatLng(-33.96065,22.46974),
                new LatLng(-33.96068,22.46993),
                new LatLng(-33.96072,22.47018),
                new LatLng(-33.96078,22.47017),
                new LatLng(-33.96086,22.47014),
                new LatLng(-33.96094,22.47012),
                new LatLng(-33.96096,22.47011),
                new LatLng(-33.96108,22.47007),
                new LatLng(-33.9611,22.47006),
                new LatLng(-33.96114,22.47003),
                new LatLng(-33.96121,22.46997),
                new LatLng(-33.96137,22.46985),
                new LatLng(-33.96144,22.4698),
                new LatLng(-33.96444,22.46751),
                new LatLng(-33.96615,22.46621),
                new LatLng(-33.96541,22.46486),
                new LatLng(-33.96492,22.46391),
                new LatLng(-33.96481,22.46371),
                new LatLng(-33.96466,22.46343),
                new LatLng(-33.96372,22.46162),
                new LatLng(-33.96317,22.46057),
                new LatLng(-33.96305,22.46036),
                new LatLng(-33.96295,22.46018),
                new LatLng(-33.96291,22.46011),
                new LatLng(-33.96284,22.45997),
                new LatLng(-33.96279,22.45988),
                new LatLng(-33.96274,22.45978),
                new LatLng(-33.96269,22.4597),
                new LatLng(-33.96262,22.45957),
                new LatLng(-33.96204,22.45849),
                new LatLng(-33.96189,22.45824),
                new LatLng(-33.96119,22.45699),
                new LatLng(-33.96114,22.45692),
                new LatLng(-33.96107,22.45681),
                new LatLng(-33.96071,22.45617),
                new LatLng(-33.96062,22.45598),
                new LatLng(-33.96053,22.45581),
                new LatLng(-33.96038,22.45553),
                new LatLng(-33.96049,22.45544),
                new LatLng(-33.9606,22.45536),
                new LatLng(-33.96101,22.45505),
                new LatLng(-33.97789,22.44187),
                new LatLng(-33.97815,22.44171),
                new LatLng(-33.97806,22.44176),
                new LatLng(-33.97812,22.44173),
                new LatLng(-33.97901,22.44129),
                new LatLng(-33.9791,22.44125),
                new LatLng(-33.97928,22.44121),
                new LatLng(-33.97953,22.44115),
                new LatLng(-33.9796,22.44114),
                new LatLng(-33.98019,22.44104),
                new LatLng(-33.98024,22.44104),
                new LatLng(-33.98039,22.44103),
                new LatLng(-33.98065,22.44101),
                new LatLng(-33.98067,22.44101),
                new LatLng(-33.9807,22.44101),
                new LatLng(-33.9808,22.44102),
                new LatLng(-33.9815,22.44109),
                new LatLng(-33.98153,22.44109),
                new LatLng(-33.98197,22.44111),
                new LatLng(-33.98198,22.44111),
                new LatLng(-33.98206,22.44112),
                new LatLng(-33.98227,22.44118),
                new LatLng(-33.98228,22.44122),
                new LatLng(-33.98231,22.44126),
                new LatLng(-33.98236,22.44131),
                new LatLng(-33.98242,22.44134),
                new LatLng(-33.98245,22.44134),
                new LatLng(-33.9825,22.44133),
                new LatLng(-33.98256,22.4413),
                new LatLng(-33.98262,22.44124),
                new LatLng(-33.98262,22.44123),
                new LatLng(-33.98271,22.44123),
                new LatLng(-33.98353,22.44126),
                new LatLng(-33.98364,22.44126),
                new LatLng(-33.98368,22.44127),
                new LatLng(-33.98373,22.44126),
                new LatLng(-33.98376,22.44125),
                new LatLng(-33.98748,22.44169),
                new LatLng(-33.98774,22.44173),
                new LatLng(-33.98831,22.44186),
                new LatLng(-33.98838,22.44187),
                new LatLng(-33.98845,22.4419),
                new LatLng(-33.98859,22.44194),
                new LatLng(-33.98873,22.44199),
                new LatLng(-33.99209,22.44319),
                new LatLng(-33.99208,22.44319),
                new LatLng(-33.99209,22.4432),
                new LatLng(-33.99212,22.44322),
                new LatLng(-33.99212,22.44323),
                new LatLng(-33.99213,22.44323),
                new LatLng(-33.99223,22.44328),
                new LatLng(-33.99279,22.44352),
                new LatLng(-33.99283,22.44354),
                new LatLng(-33.99287,22.44355),
                new LatLng(-33.9945,22.44415),
                new LatLng(-33.99456,22.44417),
                new LatLng(-33.99517,22.44436),
                new LatLng(-33.99926,22.44578)
            );

    public static PolylineOptions getPolylineCBDPacaltsdorp() {

        // loop - make the list neat with a min distance of 5 meters between locs
        for (int i = 0; i < line_cbd_ndp.getPoints().size(); i++) {
            if (i < line_cbd_ndp.getPoints().size() - 1) {
                //POINT A
                LatLng locA = line_cbd_ndp.getPoints().get(i);
                Location locationA = new Location("point A");

                locationA.setLatitude(locA.latitude);
                locationA.setLongitude(locA.longitude);

                //POINT B
                LatLng locB = line_cbd_ndp.getPoints().get(i + 1); //important the + 1 gets the next point
                Location locationB = new Location("point B");

                locationB.setLatitude(locB.latitude);
                locationB.setLongitude(locB.longitude);

                //get distance between point A and point B
                float distance = locationA.distanceTo(locationB);

                if (distance > 5) {
                    //make a list with two applicable points
                    List<LatLng> list = new ArrayList<>();
                    list.add(new LatLng(
                            line_cbd_ndp.getPoints().get(i).latitude,
                            line_cbd_ndp.getPoints().get(i).longitude));
                    list.add(new LatLng(
                            line_cbd_ndp.getPoints().get(i + 1).latitude,
                            line_cbd_ndp.getPoints().get(i + 1).longitude));
                    LatLng centroid = computeCentroid(list);

                    //rebuild the line
                    PolylineOptions  lineTemp = new PolylineOptions();
                    for (int j = 0; j < line_cbd_ndp.getPoints().size(); j++) {
                        if (j == i + 1) {
                            //add centroid
                            lineTemp.add(centroid);
                        }
                        lineTemp.add(line_cbd_ndp.getPoints().get(j));
                    }
                    line_cbd_ndp = lineTemp;
                }
            }
        }
        return line_cbd_ndp;
    }

    /*=========================== FUNCTIONS - START ===========================*/

    public static LatLng computeCentroid(List<LatLng> points) {
        double latitude = 0;
        double longitude = 0;
        int n = points.size();

        for (LatLng point : points) {
            latitude += point.latitude;
            longitude += point.longitude;
        }

        return new LatLng(latitude/n, longitude/n);
    }

    public double meterDistanceBetweenPoints(float lat_a, float lng_a, float lat_b, float lng_b) {
        float pk = (float) (180.f/Math.PI);

        float a1 = lat_a / pk;
        float a2 = lng_a / pk;
        float b1 = lat_b / pk;
        float b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;
    }

    /*=========================== FUNCTIONS - END =============================*/
}
