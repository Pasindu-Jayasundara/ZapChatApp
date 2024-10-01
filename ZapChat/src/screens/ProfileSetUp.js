import { registerRootComponent } from "expo";
import { SafeAreaView, ScrollView, StyleSheet, Text, View } from "react-native";
import { InputField } from "../components/InputField";
import { Button } from "../components/Button";
import { Profile } from "../components/Profile";

export function ProfileSetUp() {

    const design = {
        marginTop: 50,
        width: "100%"
    }

    return (
        <SafeAreaView style={styles.safearea}>

            <Text style={styles.title}>SetUp Yur Profile</Text>
            <ScrollView contentContainerStyle={styles.scrolView}>
                <View style={styles.container}>

                    <Profile/>

                    <Button text={"Next"} style={design} />
                </View>
            </ScrollView> 

        </SafeAreaView>
    )
}

registerRootComponent(ProfileSetUp)

const styles = StyleSheet.create({
    safearea: {
        flex: 1
    },
    title: {
        color: "#ff5b6b",
        fontSize: 23,
        fontWeight: "bold",
        // backgroundColor:"green",
        marginTop: 50,
        paddingLeft: 35,
        paddingVertical: 10
    },
    safearea: {
        flex: 1
    },
    scrolView: {
        flex: 1,
        justifyContent: "flex-start",
        // backgroundColor: "red",
        paddingTop:30,
        alignItems:"center"
    }
})