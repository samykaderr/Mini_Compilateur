//le premie cas son errreur
console.log("Avant try");
try{
    console.log("dans try");
    let a = 1;
} catch(e){
    console.log("attrapé", e);
} finally {
    console.log("toujours");
}
console.log("Après try");
//exemple avec erreur
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
//exemple utilisant seulemt try/catch
function test() {
    try {
        console.log("Début du bloc try");
        let result = 10 / 0; // Ceci ne lance pas d'erreur en JavaScript
        console.log("Résultat:", result);
    } catch (error) {
        console.log("Erreur attrapée:", error.message);
    }
    console.log("Fin de la fonction test");
}
//appel des fonctions
verifier();
test();
