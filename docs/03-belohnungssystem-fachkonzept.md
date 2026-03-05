# Belohnungssystem (M6) – Fachkonzept

## Ziel
Das Spiel vergibt nach erfolgreichem Abschluss eine Leistungsstufe (`Bronze`, `Silber`, `Gold`), um den Lernerfolg transparent und motivierend darzustellen.

## Geltungsbereich
Die Belohnung wird einmalig am erfolgreichen Spielende berechnet und angezeigt.
Voraussetzung für ein erfolgreiches Spielende bleibt M5 (Mindestpunkte je Themenbereich erfüllt).

## Bewertungsmodell
Grundlage ist die Gesamtleistung in Prozent:
Bewertung nach IHK-Notenbereich 1 bis 3:
$$
\text{Leistung in \%} = \frac{\text{erreichte Gesamtpunkte}}{\text{maximal erreichbare Gesamtpunkte}} \cdot 100
$$

### Punktevergabe je Fragetyp
Der Schwierigkeitsgrad wird als Faktor `s` verwendet (z. B. 1 = leicht, 2 = mittel, 3 = schwer).

- **Wahr/Falsch:**
	$$
	P_{WF} = 1 \cdot s
	$$

- **Multiple Choice:**
	$$
	P_{MC} = (2 + n_{richtig} \cdot 1) \cdot s
	$$
	mit $n_{richtig}$ = Anzahl korrekt gewählter Antworten.

- **Lückentext:**
	$$
	P_{LT} = (3 + n_{luecken} \cdot 1) \cdot s
	$$
	mit $n_{luecken}$ = Anzahl korrekt gefüllter Lücken.

## Schwellenwerte
- `Gold`: Leistung $\ge 89\%$
- `Silber`: Leistung $\ge 74\%$ und $< 89\%$
- `Bronze`: Leistung $\ge 59\%$ und $< 74\%$
- Unter $60\%$: keine Medaille

## Darstellung im Spiel
Im Win-Screen wird die erreichte Stufe klar angezeigt, z. B. „Belohnung: Silber“.

## Zeit-Bonus (Erweiterung)
Wird eine Runde innerhalb einer definierten Zielzeit abgeschlossen, erhält der Spieler zusätzliche Bonuspunkte:
$$
P_{gesamt} = P_{basis} + P_{zeitbonus}
$$

### Regel bei erneutem Spielen einer Runde
Wenn eine Runde erneut gestartet wird, wird der vorherige Zeit-Bonus **zurückgesetzt** und für den neuen Versuch neu berechnet.
Es gibt keine Stapelung mehrerer Zeit-Boni aus mehreren Versuchen.

## Optionale Erweiterungen
- **Streak-Belohnung (optional):** Zusätzliche Auszeichnung, wenn ein Spieler z. B. 5 richtige Antworten in Folge erreicht.
