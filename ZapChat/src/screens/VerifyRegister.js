import { registerRootComponent } from "expo";
import { StyleSheet, Text, View } from "react-native";
import { InputField } from "../components/InputField";
import { Button } from "../components/Button";
import { SafeAreaView } from "react-native-safe-area-context";

export function VerifyRegister() {

    const btnStyle = {

    }

    return (
        <SafeAreaView style={styles.safearea}>
            <View style={styles.container}>
                <Text style={styles.title}>Verify Mobile Number</Text>
                <Text style={styles.subtitle}>An otp number has been send your number via SMS</Text>

                <View style={styles.field}>
                    <InputField params={{ lableText: "OTP" }} />
                    <Button style={btnStyle} text={"Verify Number"} />
                </View>
            </View>
        </SafeAreaView>
    )
}

registerRootComponent(VerifyRegister)

const styles = StyleSheet.create({
    field: {
        marginTop: 40,
        rowGap:20
    },
    container: {
        justifyContent: "center",
        width: "80%"
    },
    subtitle: {
        fontSize: 15,
    },
    title: {
        color: "#ff5b6b",
        fontSize: 23,
        fontWeight: "bold",
        // backgroundColor:"green",
        marginTop: 50,
        paddingVertical: 10
    },
    safearea: {
        flex: 1,
        justifyContent: "center",
        alignItems: "center"
    },
})