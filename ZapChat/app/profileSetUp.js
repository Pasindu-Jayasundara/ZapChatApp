import { registerRootComponent } from "expo";
import { ScrollView, StyleSheet, Text, View } from "react-native";
import { InputField } from "../components/InputField";
import { Button } from "../components/Button";
import { Profile } from "../components/Profile";
import { SafeAreaView } from "react-native-safe-area-context";
import { router } from "expo-router";
import { useState } from "react";

export default function profileSetUp() {

    const [getAbout,setAbout] = useState("")
    const [getImageResult, setImageResult] = useState(null)

    const design = {
        marginTop: 20,
        width: "100%"
    }

    async function request() {

        if (getAbout.trim()=="") {
            Alert.alert("Missing About")

        } else {

            let sessionId = await AsyncStorage.getItem("user")

            let url = "https://redbird-suitable-conversely.ngrok-free.app/ZapChatBackend/Prfile"

            let formData = new FormData()
            formData.append("image",)
            formData.append("about",getAbout)

            let response = await fetch(url, {
                method: "POST",
                body:formData,
                headers: {
                    'Cookie': `JSESSIONID=${sessionId}`
                }
            })
            if (response.ok) {

                let obj = await response.json()
                if (obj.success) {

                    router.push("/profileSetUp")

                } else {
                    Alert.alert(obj.data);
                    console.log(obj.data)
                }

            } else {
                Alert.alert("Please Try Again Later");
                console.log(response)
            }

        }
    }

    return (
        <SafeAreaView style={styles.safearea}>

            <Text style={styles.title}>Setup Your Profile</Text>
            <View style={styles.scrolView}>

                <Profile getFunc={getImageResult} setFunc={setImageResult}/>

                <View style={styles.inputFields}>
                    <InputField params={{ lableText: "About", maxLength: 10 ,func:setAbout}} />

                    <Button style={design} text={"Create Profile"} func={request}/>
                </View>
            </View>

        </SafeAreaView>
    )
}

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