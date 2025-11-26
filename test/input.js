/*console.log("Avant try");
try{
    console.log("dans try");
    let a = 1;
} catch(e){
    console.log("attrapé", e);
} finally {
    console.log("toujours");
}
console.log("Après try");*/
function verifier() {
  try {
        var x = 100;
        SAMY = x + 1;
        if (x > 10) throw Trop grand;
    } catch (ACHOUCHE) {
        console.log(ACHOUCHE);
    } finally {
        return 0;
    }
}
