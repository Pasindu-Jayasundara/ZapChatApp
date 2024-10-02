import { Image, ScrollView, StyleSheet, Text, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { InputField } from "../components/InputField";
import { Button } from "../components/Button";
import { registerRootComponent } from "expo";
import { SafeAreaView } from "react-native-safe-area-context";

const logoIcon = require("../../assets/images/logo.gif");

export default function Login() {

    const design = {
        marginTop: 18
    }

    return (
        <SafeAreaView style={styles.safeAreaView}>
            <ScrollView contentContainerStyle={styles.scrollView}>

                <View style={styles.container}>

                    <View style={styles.firstView}>
                        <Image source={logoIcon} style={styles.logo} />
                        <View style={styles.textView}>
                            <Text style={styles.text1}>LogIn</Text>
                            <Text style={styles.text2}>Welcome back to ZapChat</Text>
                            <Text style={[styles.text2, styles.text3]}> Let's connect together !</Text>
                        </View>
                    </View>

                    <View style={styles.secondView}>
                        <View style={styles.fields}>
                            <InputField params={{lableText:"Mobile",inputMode:"tel",secureTextEntry:false}} />
                            <InputField params={{lableText:"Password",inputMode:"text",secureTextEntry:true}} />
                            <Button text={"Let's Go"} style={design} />
                        </View>
                    </View>

                </View>

            </ScrollView>
        </SafeAreaView>
    );

}
// registerRootComponent(Login)

const styles = StyleSheet.create({
    fields: {
        width: "75%",
        rowGap: 15,
    },
    scrollView: {
        flexGrow: 1,
        justifyContent: 'center'
    },
    container: {
        flex: 1,
        // backgroundColor: "red",
    },
    logo: {
        width: 150,
        height: 150
    },
    text3: {
        marginTop: -25,
        fontSize: 17,
        color: "#ff5b6b"
    },
    text2: {
        fontSize: 19,
        textAlign: "center"
    },
    text1: {
        fontSize: 30,
        fontWeight: "bold",
        marginTop: 25
    },
    textView: {
        justifyContent: "center",
        alignItems: "center",
        rowGap: 30,
    },
    safeAreaView: {
        flex: 1,
        backgroundColor: "white",
    },
    firstView: {
        flex: 1,
        justifyContent: "flex-end",
        // backgroundColor: "blue",
        alignItems: "center",
    },
    secondView: {
        flex: 1,
        marginTop: 60,
        // backgroundColor: "green",
        alignItems: "center"
    }
});
