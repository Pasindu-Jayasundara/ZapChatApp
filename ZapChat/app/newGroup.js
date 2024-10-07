import { FlashList } from "@shopify/flash-list";
import { Image } from "expo-image";
import { Alert, Pressable, StyleSheet, Text, TextInput, View } from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { GroupCard } from "../components/GroupCard";
import { Button } from "../components/Button";
import { Profile } from "../components/Profile";
import { InputField } from "../components/InputField";
import { useEffect, useState } from "react";
import { router } from "expo-router";
import AsyncStorage from "@react-native-async-storage/async-storage";
import Modal from "react-native-modal";


const searchIcon = require("../assets/images/search.svg")
const newGroupIcon = require("../assets/images/team.png")

export default function newGroup() {

    const [getDataArray, setDataArray] = useState([])
    const [getSearchGroupName, setSearchGroupName] = useState("")
    const [getNewGroupName, setNewGroupName] = useState("")
    const [getUser, setUser] = useState("")
    const [getIsFound, setIsFound] = useState(false)
    const [getNewGroupModalStatus, setNewGroupModalStatus] = useState(false)
    const [getGroupImage, setGroupImage] = useState(newGroupIcon)


    useEffect(() => {
        (async () => {

            let verified = await AsyncStorage.getItem("verified");
            let user = await AsyncStorage.getItem("user");

            if (verified == null || verified != "true" || user == null) {

                await AsyncStorage.removeItem("verified")
                await AsyncStorage.removeItem("user")

                router.replace("/")
            } else {
                setUser(user)
            }
        })()
    }, [])

    const search = async () => {

        let url = process.env.EXPO_PUBLIC_URL + "/NewGroup"

        if (getSearchGroupName.length > 45) {

            let obj = {
                name: getSearchGroupName,
            }
            let response = await fetch(url, {
                method: "POST",
                body: JSON.stringify(obj),
                headers: {
                    "Content-Type": "application/json",
                    'Cookie': `JSESSIONID=${getUser}`
                }
            })

            if (response.ok) {

                let obj = await response.json()
                if (obj.success) {

                    if (obj.data.isFound) {

                        setIsFound(true)
                        setDataArray(obj.data.data)

                    } else {
                        setDataArray([])
                    }

                } else {
                    if (obj.data == "Please LogIn") {

                        await AsyncStorage.removeItem("verified");
                        await AsyncStorage.removeItem("user");

                        router.replace("/")
                    } else {
                        Alert.alert(obj.data);
                    }
                    console.log(obj.data)
                }

            } else {
                Alert.alert("Please Try Again Later");
                console.log(response)
            }
        } else {
            Alert.alert("Group Name Too Long");
        }

    }

    const addNewGroup = async ()=>{

    }

    return (
        <SafeAreaView style={styles.body}>

            <View style={styles.head}>
                <TextInput
                    placeholder="Enter Group Name .."
                    numberOfLines={1}
                    style={styles.input}
                    onChangeText={(text) => { setSearchGroupName(text) }}
                    value={getSearchGroupName}
                    inputMode="text"
                />

                <Pressable onPress={search}>
                    <Image source={searchIcon} style={styles.icon} />
                </Pressable>
            </View>

            <Modal
                isVisible={getNewGroupModalStatus}
                onBackButtonPress={() => { setNewGroupModalStatus(false) }}
                onBackdropPress={() => { setNewGroupModalStatus(false) }}
            >

                <View style={styles.modalView}>
                    <Profile getFunc={getGroupImage} setFunc={setGroupImage} />
                    <InputField params={{ lableText: "Group Name", secureTextEntry: false, inputMode: "text", maxLength: 45, func: setNewGroupName, getFunc: getNewGroupName }} />

                    <Button text={"Create +"} style={[styles.newButton, styles.create]} func={addNewGroup} />

                </View>


            </Modal>

            {getNewGroupModalStatus ? "" : (
                <Button text={"Create a New Group"} style={styles.newButton} func={() => { setNewGroupModalStatus(true) }} />
            )}

            {getIsFound ? (

                <FlashList
                    renderItem={({ item }) => <GroupCard data={item} />}
                    data={getDataArray}
                    keyExtractor={item => item.userId}
                    contentContainerStyle={styles.list}
                    estimatedItemSize={200}
                />

            ) : (
                <View style={styles.noView}>
                    <Text style={styles.noText}>No Groups !</Text>
                </View>
            )}

        </SafeAreaView>
    )
}

const styles = StyleSheet.create({
    create: {
        marginTop: 30
    },
    modalView: {
        justifyContent: "center",
        alignItems: "center"
    },
    newButton: {
        alignSelf: "center",
    },
    noText: {
        fontSize: 20,
        fontWeight: "bold",
        color: "#919190"
    },
    noView: {
        width: "100%",
        alignItems: "center",
        flex: 1,
        justifyContent: "center",
    },
    icon: {
        width: 22,
        height: 22,
    },
    head: {
        flexDirection: "row",
        columnGap: 15,
        justifyContent: "center",
        alignItems: "center",
        marginBottom: 20
    },
    input: {
        backgroundColor: "#e3e3e3",
        borderRadius: 5,
        minHeight: 40,
        maxHeight: 120,
        paddingHorizontal: 10,
        color: "#ff5b6b",
        width: "80%",
        paddingVertical: 2
    },
    body: {
        flex: 1
    }
})