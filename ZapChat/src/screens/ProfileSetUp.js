import { registerRootComponent } from "expo";
import { SafeAreaView, ScrollView, StyleSheet, Text, View } from "react-native";
import { InputField } from "../components/InputField";
import { Button } from "../components/Button";
import { Profile } from "../components/Profile";

export function ProfileSetUp() {

    const design = {
        marginTop: 20,
        width: "100%"
    }

    return (
        <SafeAreaView style={styles.safearea}>

            <Text style={styles.title}>Setup Your Profile</Text>
            <View style={styles.scrolView}>

                <Profile />

                <View style={styles.inputFields}>
                    <InputField params={{ lableText: "About", maxLength: 10 }} />

                    <Button style={design} text={"Create Profile"}/>
                </View>
            </View>

        </SafeAreaView>
    )
}

registerRootComponent(ProfileSetUp)

const styles = StyleSheet.create({
    inputFields: {
        width: "80%",
        marginTop: 70
    },
    safearea: {
        flex: 1
    },
    title: {
        color: "#ff5b6b",
        fontSize: 23,
        fontWeight: "bold",
        marginTop: 75,
        paddingLeft: 35,
    },
    safearea: {
        flex: 1
    },
    scrolView: {
        flex: 1,
        // justifyContent: "center",
        // backgroundColor: "red",
        alignItems: "center",
        paddingTop:90
    }
})