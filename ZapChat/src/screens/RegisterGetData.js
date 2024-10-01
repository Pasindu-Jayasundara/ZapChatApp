import { registerRootComponent } from "expo";
import { ScrollView, StyleSheet, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { InputField } from "../components/InputField";
import { Button } from "../components/Button";

export function RegisterGetData() {

    const design = {
        marginTop: 50,
        width: "100%"
    }

    return (
        <SafeAreaView style={styles.safearea}>
            <Text style={styles.title}>Fill Your Details</Text>
            <ScrollView contentContainerStyle={styles.scrolView}>
                <View style={styles.container}>
                    <InputField params={{ lableText: "First Name", inputMode: "text", secureTextEntry: false }} />
                    <InputField params={{ lableText: "Last Name", inputMode: "text", secureTextEntry: false }} />
                    <InputField params={{ lableText: "Mobile Number", inputMode: "tel", secureTextEntry: false }} />
                    <InputField params={{ lableText: "Password", inputMode: "text", secureTextEntry: true }} />
                    <InputField params={{ lableText: "Re-type Password", inputMode: "text", secureTextEntry: true }} />

                    <Button text={"Next"} style={design} />
                </View>
            </ScrollView>
        </SafeAreaView>
    )
}

// registerRootComponent(RegisterGetData)

const styles = StyleSheet.create({
    container:{
        flex:1,
        alignItems:"center",
        width:"80%",
        rowGap:22
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