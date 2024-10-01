import { registerRootComponent } from "expo";
import { SafeAreaView, StyleSheet, View } from "react-native";

export function ProfileSetUp(){
    return(
        <SafeAreaView style={styles.safearea}>

        </SafeAreaView>
    )
}

registerRootComponent(ProfileSetUp)

const styles=StyleSheet.create({
    safearea: {
        flex: 1
    },
})